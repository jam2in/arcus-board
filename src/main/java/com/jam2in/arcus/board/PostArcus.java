package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.model.PostInfo;
import com.jam2in.arcus.board.repository.PostRepository;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.collection.*;
import net.spy.memcached.internal.CollectionFuture;
import net.spy.memcached.internal.CollectionGetBulkFuture;
import net.spy.memcached.ops.CollectionOperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.VfsUtils;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class PostArcus {
    private static final Logger logger = LoggerFactory.getLogger(PostArcus.class);
    private ArcusClient arcusClient;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BoardArcus boardArcus;

    public PostArcus() {
        this.arcusClient = Application.arcusClient;
    }

    public List<Post> getPosts(int board_id, int startList, int pageSize) {
        List<Post> posts = null;
        String key = "board"+board_id;
        int count = 20;
        ElementFlagFilter filter = ElementFlagFilter.DO_NOT_FILTER;
        CollectionFuture<Map<Integer, Element<Object>>> future = null;
        Map<Integer, Element<Object>> elements = null;
        future = arcusClient.asyncBopGetByPosition(key, BTreeOrder.DESC, startList*pageSize, startList*pageSize + count-1);

        try {
            elements = future.get(1000, TimeUnit.MILLISECONDS);

            CollectionResponse response = future.getOperationStatus().getResponse();
            //item doesn't exist
            if(response.equals(CollectionResponse.NOT_FOUND)) {
                boardArcus.bopCreateBoard(board_id);
                this.setPosts(board_id);
                logger.info("getPosts() : " + response.toString());
                return getPosts(board_id, startList, pageSize);
            }
            //elements don't exist
            else if(response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                logger.info("getPosts() : " + response.toString());
                return null;
            }

            else {
                posts = new ArrayList<Post>();
                for (Map.Entry<Integer, Element<Object>> each : elements.entrySet()) {
                    //logger.info("element read : "+ each.getKey().toString());
                    //logger.info("ee"+each.getValue().toString());
                    PostInfo postInfo = (PostInfo)each.getValue().getValue();
                    posts.add(postInfo.getPost());
                }
                logger.info("getPosts() : " + response.toString());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return posts;
    }


    public void setPosts(int board_id) {
        List<Post> posts = postRepository.selectPage(board_id, 0, 20);
        String key = "board"+board_id;
        List<Element<Object>> elements = new ArrayList<Element<Object>>();

        if (posts.size() > arcusClient.getMaxPipedItemCount()) {
            logger.error("PIPE_ERROR memory overflow");
            return;
        }

        for (Post post:posts) {
            PostInfo postInfo = new PostInfo(post);
            elements.add(new Element<Object>(postInfo.getId(), postInfo, new byte[]{1,1}));
            //logger.info("element added: "+ post.getId());
        }

        CollectionFuture<Map<Integer, CollectionOperationStatus>> future = null;

        try {
            future = arcusClient.asyncBopPipedInsertBulk(key, elements, new CollectionAttributes());
        } catch (IllegalStateException e) {
        }

        if (future == null) return;

        try {
            Map<Integer, CollectionOperationStatus> result = future.get(1000L, TimeUnit.MILLISECONDS);

            if (!result.isEmpty()) {
                for (Map.Entry<Integer, CollectionOperationStatus> entry : result.entrySet()) {
                    logger.error("failed element = " + elements.get(entry.getKey()));
                    logger.error(", caused by : " + entry.getValue().getResponse());
                }
            }
            else {
                logger.info("all inserted");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
    public Post getPostInfo(int id) {
        Post post = null;
        PostInfo postInfo = null;
        CollectionFuture<Map<Long, Element<Object>>> future = null;
        String key = "board1";
        ElementFlagFilter filter = new ElementFlagFilter(ElementFlagFilter.CompOperands.Equal, new byte[] {1, 1});

        future = arcusClient.asyncBopGet(key, (long)id, filter, false, false);
        if (future == null) return null;
        try {
            Map<Long, Element<Object>> result = future.get(1000L, TimeUnit.MILLISECONDS);

            CollectionResponse response = future.getOperationStatus().getResponse();
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                logger.info("getpostInfo() : NOT_FOUND");
                return null;
            } else if (response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                logger.info("getpostInfo() : NOT_FOUND_ELEMENT");
                return null;
            }
            postInfo = (PostInfo) result.get((long) id).getValue();

            logger.info("get postInfo : #{}", postInfo.getId());
            post = postInfo.getPost();
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        if (postInfo == null) {
            return null;
        }
        return post;
    }

    public boolean setPostInfo(Post post) {
        boolean setSuccess = false;
        CollectionFuture<Boolean> future = null;
        CollectionAttributes attributes = new CollectionAttributes();
        String key = "board1";
        PostInfo postInfo = new PostInfo(post);

        try {
            future = arcusClient.asyncBopInsert(key, (long)post.getId(), new byte[]{1,1}, postInfo, attributes);
    } catch (IllegalStateException e) {
        logger.error(e.getMessage());
    }

        if (future == null) return false;

        try {
            setSuccess = future.get(1000L, TimeUnit.MILLISECONDS); //timeout 1초
            logger.info("set postInfo : {}", future.getOperationStatus().getResponse());
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return setSuccess;
    }

    public String getPostContent(int id) {
        Future<Object> future = null;
        String key = "postC"+id;
        String post = null;

        future = this.arcusClient.asyncGet(key);
        if (future == null) return null;
        try {
            post = (String)future.get();
            logger.info("getPostContent(): {}", post);
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return post;
    }

    public boolean setPostContent(int id, String post) {
        Future<Boolean> future = null;
        String key = "postC"+id;
        boolean setSuccess = false;

        try {
            future = this.arcusClient.set(key, 300,post);
            logger.info("setPostContent(): #{}", key);
        } catch (Exception e) {
            logger.error("setPostContent(): {}", e.getMessage());
        }

        try {
            setSuccess = future.get(700L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return setSuccess;
    }

    public boolean delPostContent(int id) {
        boolean deleteSuccess = false;
        Future<Boolean> future = null;
        String key = "post"+id;

        future = this.arcusClient.delete(key);
        try {
            deleteSuccess = future.get();
            logger.info("delPostContent(): #{}", key);
        } catch (Exception e) {
            logger.error("delPostContent(): {}", e.getMessage());
        }
        return deleteSuccess;
    }
}
