package com.jam2in.arcus.board;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.collection.CollectionAttributes;
import net.spy.memcached.collection.CollectionOverflowAction;
import net.spy.memcached.collection.ElementValueType;
import net.spy.memcached.internal.CollectionFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class BoardArcus {
    private static final Logger logger = LoggerFactory.getLogger(BoardArcus.class);
    public static long N = 20;
    private static long MAX = 20*100;
    private ArcusClient arcusClient;

    BoardArcus() {
        this.arcusClient = Application.arcusClient;
    }

    public boolean bopCreateBoard(int id) {
        boolean setSuccess = false;
        CollectionFuture<Boolean> future = null;
        String key="board"+id;
        CollectionAttributes attributes = new CollectionAttributes(300, MAX, CollectionOverflowAction.smallest_trim);
        attributes.setExpireTime(300);

        future = arcusClient.asyncBopCreate(key, ElementValueType.OTHERS, attributes);

        if (future == null) return false;

        try {
            setSuccess = future.get(1000L, TimeUnit.MILLISECONDS);
            logger.info("bopCreateBoard(): {}", future.getOperationStatus().getResponse());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        }

        return setSuccess;
    }

    public void bopDelBoard(int id) {
        String key = "board"+id;
        try {
            Future<Boolean> future = arcusClient.delete(key);
            logger.info("bopDelBoard(): {}", future.get().toString());
        } catch (Exception e) {
            logger.error("bopDelBoard(): {}", e.getMessage());
        }
    }
}
