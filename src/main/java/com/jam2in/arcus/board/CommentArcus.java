package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
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
public class CommentArcus {
    private static final Logger logger = LoggerFactory.getLogger(PostArcus.class);
    private ArcusClient arcusClient;
    private static int MAX = 20*3;
    @Autowired
    private CommentRepository commentRepository;

    public CommentArcus() {
        this.arcusClient = Application.commentArcusClient;
    }

    public boolean bopCreateCmt(int post_id) {
        boolean result = false;
        String key = "Comment:"+post_id;

        CollectionAttributes attributes = new CollectionAttributes(300, (long)MAX, CollectionOverflowAction.smallest_trim);
        CollectionFuture<Boolean> future = arcusClient.asyncBopCreate(key, ElementValueType.OTHERS, attributes);

        try {
            result = future.get(1000L, TimeUnit.MILLISECONDS);
            logger.info("bopCreateCmt() : " + future.getOperationStatus().getResponse().toString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }

        return result;
    }

    public List<Comment> getComments(int post_id, int startList, int pageSize) {
        List<Comment> comments = null;
        String key = "Comment:"+post_id;
        CollectionFuture<Map<Integer, Element<Object>>> future = null;
        Map<Integer, Element<Object>> elements = null;
        CollectionFuture<Integer> cntFuture = null;

        cntFuture = arcusClient.asyncBopGetItemCount(key, 0, Integer.MAX_VALUE, ElementFlagFilter.DO_NOT_FILTER);
        try {
            if (cntFuture.get() != null) {
                int count = cntFuture.get();
                //logger.info("count:{}, startList:{}", count, startList);
                if (startList+pageSize > MAX) {
                    setComments(post_id, count, MAX-count);
                }
                if (count <= startList && startList != MAX) {
                    setComments(post_id, count, startList+pageSize-count);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        future = arcusClient.asyncBopGetByPosition(key, BTreeOrder.DESC, startList, startList+pageSize-1);
        try {
            elements = future.get();

            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("getComments(): " + response.toString());
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                if (!setComments(post_id, startList, pageSize)) return null;
                return getComments(post_id, startList, pageSize);
            }
            else if (response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                return null;
            }
            comments = new ArrayList<Comment>();
            for (Map.Entry<Integer, Element<Object>> each : elements.entrySet()) {
                logger.info("element read : "+ each.getValue().toString());
                Comment comment = (Comment)each.getValue().getValue();
                comments.add(comment);
            }
            logger.info(response.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return comments;
    }
    public boolean setComments(int post_id, int startList, int pageSize) {
        boolean result = false;
        List<Comment> comments = commentRepository.selectPage(post_id, startList, pageSize);
        String key = "Comment:"+post_id;
        CollectionFuture<Map<Integer, CollectionOperationStatus>> future = null;
        List<Element<Object>> elements = new ArrayList<>();

        if(comments.size() == 0) return false;
        if(comments.size() > arcusClient.getMaxPipedItemCount()) {
            logger.error("PIPE_ERROR memory overflow");
            return false;
        }

        for(Comment comment: comments) {
            elements.add(new Element<Object>(comment.getId(), comment, new byte[]{1,1}));
            //logger.info("element added: "+ post.getId());
        }

        future = arcusClient.asyncBopPipedInsertBulk(key, elements, new CollectionAttributes(300, (long)MAX, CollectionOverflowAction.smallest_trim));

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
                logger.info("setComments() : all inserted");
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return result;
    }
    public Comment getComment(int id, int post_id) {
        Comment comment = null;
        String key = "Comment:"+post_id;
        CollectionFuture<Map<Long, Element<Object>>> future = arcusClient.asyncBopGet(key, (long)id, ElementFlagFilter.DO_NOT_FILTER, false, false);

        try {
            Map<Long, Element<Object>> result = future.get(1000L, TimeUnit.MILLISECONDS);
            CollectionResponse response = future.getOperationStatus().getResponse();
            if (result == null) return null;
            comment = (Comment)result.get((long)id).getValue();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        return comment;
    }
    public void setComment(Comment comment) {
        String key = "Comment:"+comment.getPost_id();
        CollectionFuture<Boolean> future = arcusClient.asyncBopInsert(key, comment.getId(), new byte[]{1,1}, comment, null);

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("setComment(): " + response.toString());
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                return;
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
    public void deleteComment(int id, int post_id) {
        String key = "Comment:"+post_id;
        CollectionFuture<Boolean> future = arcusClient.asyncBopDelete(key, id, ElementFlagFilter.DO_NOT_FILTER, false);

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("deleteComment(): "+response.toString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }

    }
    public void updateComment(Comment updated) {
        String key = "Comment:"+updated.getPost_id();
        Comment comment = getComment(updated.getId(), updated.getPost_id());
        comment.setContent(updated.getContent());
        CollectionFuture<Boolean> future = arcusClient.asyncBopUpdate(key, comment.getId(), null, comment);

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
            CollectionResponse response = future.getOperationStatus().getResponse();
            logger.info("updatePostInfo(): {}", response.toString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
}
