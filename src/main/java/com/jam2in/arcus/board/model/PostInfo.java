package com.jam2in.arcus.board.model;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;

@Component
public class PostInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int user_id;
    private String user_name;
    private int board_id;
    private int no;
    private String title;
    private Timestamp updated_date;
    private int views;

    public PostInfo(Post post) {
            this.id = post.getId();
            this.user_id = post.getUser_id();
            this.user_name = post.getUser_name();
            this.board_id = post.getBoard_id();
            this.no = post.getNo();
            this.title = post.getTitle();
            this.updated_date = post.getUpdated_date();
            this.views = post.getViews();
    }
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Timestamp updated_date) {
        this.updated_date = updated_date;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Post getPost() {
        Post post = new Post();

        post.setId(this.getId());
        post.setUser_id(this.getUser_id());
        post.setUser_name(this.getUser_name());
        post.setBoard_id(this.getBoard_id());
        post.setNo(this.getNo());
        post.setTitle(this.getTitle());
        post.setUpdated_date(this.getUpdated_date());
        post.setViews(this.getViews());

        return post;
    }
}
