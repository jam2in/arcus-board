package com.jam2in.arcus.board;

import com.jam2in.arcus.board.model.Post;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.ConnectionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Base64;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class PostArcus {
    public static final Logger logger = LoggerFactory.getLogger(PostArcus.class);

    String arcusAdmin;
    String serviceCode;
    ArcusClient arcusClient;

    public PostArcus() {
        this.arcusAdmin = arcusAdmin;
        this.serviceCode = serviceCode;
        this.arcusClient = ArcusClient.createArcusClient("1.255.51.181:8080", "test", new ConnectionFactoryBuilder());
    }

    public byte[] serialize(Object object) {
        byte[] serializedMember = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(object);
                serializedMember = baos.toByteArray();
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return serializedMember;
    }

    public Object deserialize(byte[] post) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(post)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object objectPost = ois.readObject();
                return (Post)objectPost;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean setPost(Post post) {
        Future<Boolean> future = null;
        boolean setSuccess = false;
        String key = "post"+post.getId();

        try {
            future = this.arcusClient.set(key, 600, post);
            logger.info("[ARCUS] set : {}", key);
        } catch (Exception e) {
            logger.error("[ARCUS] set-exception: {}", e.getMessage());
        }

        if (future == null) {logger.error("[ARCUS] set null");}
        try {
            setSuccess = future.get(700L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.info("[ARCUS] set-exception");
        }

        return setSuccess;
    }

    public Post getPost(int id) {
        Future<Object> future = null;
        String key = "post"+id;
        Post post = null;
        future = this.arcusClient.asyncGet(key);

        try {
           post = (Post)future.get(700L, TimeUnit.MILLISECONDS);
            // byte[] strPost = (byte []) future.get(700L, TimeUnit.MILLISECONDS);
           // post = (Post)deserialize(strPost);
            logger.info("[ARCUS] get : "+post.getId());
        } catch (Exception e) {
            logger.error("[ARCUS] get exception : " + e.getMessage());
        }
        return post;
    }


}
