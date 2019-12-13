package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.model.PostInfo;
import com.jam2in.arcus.board.repository.PostRepository;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.collection.*;
import net.spy.memcached.internal.CollectionFuture;
import net.spy.memcached.ops.CollectionOperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
        this.arcusClient = Application.boardArcusClient;
    }

    public List<Post> getPosts(int board_id, int startList, int pageSize) {
        List<Post> posts = null;
        String key = "Board:"+board_id;
        CollectionFuture<Map<Integer, Element<Object>>> future = null;
        CollectionFuture<Integer> cntFuture = null;
        Map<Integer, Element<Object>> elements = null;

        cntFuture = arcusClient.asyncBopGetItemCount(key, 0, (long)Integer.MAX_VALUE, ElementFlagFilter.DO_NOT_FILTER);
        try {
            if (cntFuture.get()!=null) {
                int count = cntFuture.get();
                if (startList+pageSize> BoardArcus.MAX) {
                    setPosts(board_id, count, (int)BoardArcus.MAX - count);
                    return null;
                }
                if (count <= startList && startList != BoardArcus.MAX) {
                    setPosts(board_id, count, startList + pageSize - count);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        future = arcusClient.asyncBopGetByPosition(key, BTreeOrder.DESC, startList, startList + pageSize-1);

        try {
            elements = future.get(1000, TimeUnit.MILLISECONDS);

            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("getPosts(){}: {}", key, response.toString());

            if(response.equals(CollectionResponse.NOT_FOUND)) {
                if (!setPosts(board_id, startList, pageSize)) return null;
                return getPosts(board_id, startList, pageSize);
            }

            //elements don't exist
            else if(response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                return null;
            }

            else {
                posts = new ArrayList<Post>();
                for (Map.Entry<Integer, Element<Object>> each : elements.entrySet()) {
                    logger.info("element read : "+ each.getKey().toString() + "//VALUE:" + each.getValue().toString());
                    PostInfo postInfo = (PostInfo)each.getValue().getValue();
                    posts.add(postInfo.getPost());
                }
            }

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return posts;
    }
    public boolean setPosts(int board_id, int startList, int pageSize) {
        boolean result = false;
        List<Post> posts = postRepository.selectPage(board_id, startList, pageSize);
        String key = "Board:"+board_id;
        List<Element<Object>> elements = new ArrayList<Element<Object>>();
        int size;

        if ((size = posts.size()) == 0) {
            return false;
        }
        if (size > arcusClient.getMaxPipedItemCount()) {
            logger.info("setPosts(): maxPipedItem");
            return false;
        }

        for (Post post:posts) {
            PostInfo postInfo = new PostInfo(post);
            elements.add(new Element<Object>((long)postInfo.getId(), postInfo, new byte[]{1,1}));
            //logger.info("element added: "+ post.getTitle());
        }

        CollectionFuture<Map<Integer, CollectionOperationStatus>> future = null;

        try {
            future = arcusClient.asyncBopPipedInsertBulk(key, elements,
                    new CollectionAttributes(300, BoardArcus.MAX, CollectionOverflowAction.smallest_trim));
        } catch (IllegalStateException e) {
        }

        if (future == null) return false;

        try {
            Map<Integer, CollectionOperationStatus> mapResult = future.get(1000L, TimeUnit.MILLISECONDS);

            if (!mapResult.isEmpty()) {
                for (Map.Entry<Integer, CollectionOperationStatus> entry : mapResult.entrySet()) {
                    logger.error("failed element = " + elements.get(entry.getKey()));
                    logger.error(", caused by : " + entry.getValue().getResponse());
                }
            }
            else {
                result = true;
                logger.info("all inserted");
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return result;
    }

    public Post getPostInfo(int id, int board_id) {
        Post post = null;
        PostInfo postInfo = null;
        CollectionFuture<Map<Long, Element<Object>>> future = null;
        String key = "Board:"+board_id;
        ElementFlagFilter filter = new ElementFlagFilter(ElementFlagFilter.CompOperands.Equal, new byte[] {1, 1});

        future = arcusClient.asyncBopGet(key, (long)id, filter, false, false);
        if (future == null) return null;
        try {
            Map<Long, Element<Object>> result = future.get(1000L, TimeUnit.MILLISECONDS);

            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("getpostInfo(): "+response.toString());
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                return null;
            }
            if (response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                return null;
            }
            postInfo = (PostInfo) result.get((long) id).getValue();

            logger.info("get postInfo : #{}", postInfo.getId());
            post = postInfo.getPost();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        if (postInfo == null) {
            return null;
        }
        return post;
    }
    public void setPostInfo(Post post) {
        CollectionFuture<Boolean> future = null;
        String key = "Board:"+post.getBoard_id();
        PostInfo postInfo = new PostInfo(post);

        try {
            future = arcusClient.asyncBopInsert(key, (long)post.getId(), new byte[]{1,1}, postInfo, null);
        } catch (IllegalStateException e) {
        logger.error(e.getMessage());
        }

        if (future == null) return;

        try {
            future.get(1000L, TimeUnit.MILLISECONDS); //timeout 1초
            logger.info("set postInfo : {} {}", future.getOperationStatus().getResponse(), post.toString());
            CollectionResponse response = future.getOperationStatus().getResponse();
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                return;
            }
            if (response.equals(CollectionResponse.OVERFLOWED)) {
                return;
            }
            if (response.equals(CollectionResponse.OUT_OF_RANGE)) {
                return;
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
    public void delPostInfo(int id, int board_id) {
        CollectionFuture<Boolean> future = null;
        String key = "Board:"+board_id;

        future = arcusClient.asyncBopDelete(key, id, ElementFlagFilter.DO_NOT_FILTER, false);
        try {
            future.get();
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("delPostInfo(): {}", response.toString());
        } catch (Exception e) {
            logger.error("delPostInfo(): {}", e.getMessage());
        }
    }
    public boolean updatePostInfo(int id, int board_id, String title, String content) {
        Boolean result = false;
        String key = "Board:"+board_id;
        Post post = getPostInfo(id, board_id);
        post.setTitle(title);
        post.setContent(content);
        CollectionFuture<Boolean> future = arcusClient.asyncBopUpdate(key, id, null, new PostInfo(post));

        try {
            result = future.get();
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("updatePostInfo(): {}", response.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean updatePostViews(Post post) {
        Boolean result = false;
        int id = post.getId();
        int board_id = post.getBoard_id();
        String key = "Board:"+board_id;

        if (post == null) return false;

        post.setViews(post.getViews()+1);
        CollectionFuture<Boolean> future = arcusClient.asyncBopUpdate(key, id, null, new PostInfo(post));
        try {
            result = future.get();
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("updatePostViews(): {}", response.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getPostContent(int id) {
        Future<Object> future = null;
        String key = "Board:post"+id;
        String post = null;

        future = arcusClient.asyncGet(key);
        if (future == null) return null;
        try {
            post = (String)future.get();
            logger.info("getPostContent(): {}", post);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return post;
    }
    public boolean setPostContent(int id, String post) {
        Future<Boolean> future = null;
        String key = "Board:post"+id;
        boolean result = false;

        try {
            future = arcusClient.set(key, 300,post);
            logger.info("setPostContent(): #{}", key);
        } catch (Exception e) {
            logger.error("setPostContent(): {}", e.getMessage());
        }

        try {
            result = future.get(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return result;
    }
    public void delPostContent(int id) {
        Future<Boolean> future = null;
        String key = "Board:post"+id;

        future = arcusClient.delete(key);
        try {
            logger.info("delPostContent(): {}", future.get().toString());
        } catch (Exception e) {
            logger.error("delPostContent(): {}", e.getMessage());
        }
    }

    public void updatePostContent(int id, String content) {
        Future<Boolean> future = null;
        String key = "Board:post"+id;

        future = arcusClient.replace(key, 300, content);
        logger.info("updatePostContent(): #{},{}",key, future.toString());

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
}
