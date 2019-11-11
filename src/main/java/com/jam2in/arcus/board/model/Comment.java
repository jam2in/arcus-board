package com.jam2in.arcus.board.model;

public class Comment {

    private int id;
    private int user_id;
    private int post_id;

    /*
    private int group;

    private int sequence;

    private int level;
     */

    private int content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }
}
