package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Board;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.collection.CollectionAttributes;
import net.spy.memcached.collection.ElementValueType;
import net.spy.memcached.internal.CollectionFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class BoardArcus {
    public static final Logger logger = LoggerFactory.getLogger(BoardArcus.class);
    private ArcusClient arcusClient;

    BoardArcus() {
        this.arcusClient = Application.arcusClient;
    }

    public boolean bopCreateBoard(int id) {
        boolean setSuccess = false;
        CollectionFuture<Boolean> future = null;
        String key="board"+id;
        CollectionAttributes attributes = new CollectionAttributes();
        attributes.setExpireTime(300);

        future = arcusClient.asyncBopCreate(key, ElementValueType.OTHERS, attributes);

        if (future == null) return false;

        try {
            setSuccess = future.get(1000L, TimeUnit.MILLISECONDS);
            logger.info("{}", future.getOperationStatus().getResponse());
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

    public Board bopGetBoard(int id) {
        Board board = null;

        return board;
    }
}
