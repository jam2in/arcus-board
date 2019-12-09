package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.collection.*;
import net.spy.memcached.internal.CollectionFuture;
import net.spy.memcached.ops.CollectionOperationStatus;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.VfsUtils;
import org.springframework.stereotype.Component;
import sun.security.x509.FreshestCRLExtension;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class CommentArcus {
    private static final Logger logger = LoggerFactory.getLogger(PostArcus.class);
    private ArcusClient arcusClient;
    @Autowired
    CommentRepository commentRepository;

    public CommentArcus() {
        this.arcusClient = Application.arcusClient;
    }

    public boolean bopCreateCmt(int post_id) {
        boolean result = false;
        String key = "cmt"+post_id;
        String post_key = "post"+post_id;
        long N = 20;
        long MAX= N * 100;
        Future<Object> post = arcusClient.asyncGet(post_key);

        // search for existing post element in b+tree
        if (post == null) return false;
        try {
            if (post.get() == null) {
                logger.info("Post b+tree NOT FOUND");
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            post.cancel(true);
            return false;
        }

        CollectionAttributes attributes = new CollectionAttributes(300, MAX, CollectionOverflowAction.smallest_trim);
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
        String key = "cmt"+post_id;
        CollectionFuture<Map<Integer, Element<Object>>> future = null;
        Map<Integer, Element<Object>> elements = null;

        future = arcusClient.asyncBopGetByPosition(key, BTreeOrder.DESC, startList, startList+pageSize-1);
        try {
            elements = future.get();

            CollectionResponse response = future.getOperationStatus().getResponse();
            if (response.equals(CollectionResponse.NOT_FOUND)) {
                logger.info("getComments(): " + response.toString());
                if (!bopCreateCmt(post_id)) return null;
                setComments(post_id);
                return getComments(post_id, startList, pageSize);
            }
            else if (response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) {
                logger.info("getComments(): " + response.toString());
                return null;
            }
            comments = new ArrayList<Comment>();
            for (Map.Entry<Integer, Element<Object>> each : elements.entrySet()) {
                logger.info("element read : "+ each.getValue().toString());
                Comment comment = (Comment)each.getValue().getValue();
                comments.add(comment);
            }
            logger.info("getComments(): " + response.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return comments;
    }
    public void setComments(int post_id) {
        List<Comment> comments = commentRepository.selectPage(post_id, 0, (int)BoardArcus.N);
        String key = "cmt"+post_id;
        CollectionFuture<Map<Integer, CollectionOperationStatus>> future = null;
        List<Element<Object>> elements = new ArrayList<>();

        if(comments.size() > arcusClient.getMaxPipedItemCount()) {
            logger.error("PIPE_ERROR memory overflow");
            return;
        }
        if(comments.size() == 0) return;

        for(Comment comment: comments) {
            elements.add(new Element<Object>(comment.getId(), comment, new byte[]{1,1}));
            //logger.info("element added: "+ post.getId());
        }

        future = arcusClient.asyncBopPipedInsertBulk(key, elements, null);

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
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
    public Comment getComment(int id, int post_id) {
        Comment comment = null;
        String key = "cmt"+post_id;
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
        String key = "cmt"+comment.getPost_id();
        CollectionFuture<Boolean> future = arcusClient.asyncBopInsert(key, comment.getId(), new byte[]{1,1}, comment, null);

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
            logger.info("setComment(): " + future.getOperationStatus().getResponse().toString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }
    }
    public void deleteComment(int id, int post_id) {
        String key = "cmt"+post_id;
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
        String key = "cmt"+updated.getPost_id();
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
