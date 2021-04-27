package com.jam2in.arcus.board.model;


public class Board {
    private int bid;
    private String name;
    private int category;
    private int reqRecent;
    private int reqToday;

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getReqRecent() {
        return reqRecent;
    }

    public void setReqRecent(int reqRecent) {
        this.reqRecent = reqRecent;
    }

    public int getReqToday() {
        return reqToday;
    }

    public void setReqToday(int reqToday) {
        this.reqToday = reqToday;
    }
}
