package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void insertPost(Post post) {
        postRepository.insert(post);
    }

    public void updatePost(Post post) {
        postRepository.update(post);
    }

    public void deletePost(int id) {
        postRepository.delete(id);
    }

    public Post selectOnePost(int id) {
        return postRepository.selectOne(id);
    }

    public List<Post> selectAll(int bid, int startList, int pageSize) {
        return postRepository.selectAll(bid, startList, pageSize);
    }

    public List<Post> selectCategory(int bid, int category, int startList, int pageSize) {
        return postRepository.selectCategory(bid, category, startList, pageSize);
    }

    public List<Post> selectLikesAll() {
        return postRepository.selectLikesAll();
    }
    public List<Post> selectLikesMonth() {
        return postRepository.selectLikesMonth();
    }
    public List<Post> selectLikesToday() {
        return postRepository.selectLikesToday();
    }

    public List<Post> selectViewsAll() {
        return postRepository.selectViewsAll();
    }
    public List<Post> selectViewsMonth() {
        return postRepository.selectViewsMonth();
    }
    public List<Post> selectViewsToday() {
        return postRepository.selectViewsToday();
    }

    public List<Post> selectLikesAllBoard(int bid) {
        return postRepository.selectLikesAllBoard(bid);
    }
    public List<Post> selectLikesMonthBoard(int bid) {
        return postRepository.selectLikesMonthBoard(bid);
    }
    public List<Post> selectLikesTodayBoard(int bid) {
        return postRepository.selectLikesTodayBoard(bid);
    }

    public List<Post> selectViewsAllBoard(int bid) {
        return postRepository.selectViewsAllBoard(bid);
    }
    public List<Post> selectViewsMonthBoard(int bid) {
        return postRepository.selectViewsMonthBoard(bid);
    }
    public List<Post> selectViewsTodayBoard(int bid) {
        return postRepository.selectViewsTodayBoard(bid);
    }

    public int countPost(int bid) {
        return postRepository.countPost(bid);
    }

    public int countPostCategory(int bid, int category) {
        return postRepository.countPostCategory(bid, category);
    }

    public void increaseCmt(int pid) {
        postRepository.increaseCmt(pid);
    }

    public void decreaseCmt(int pid) {
        postRepository.decreaseCmt(pid);
    }

    public void increaseViews(int id) {
        postRepository.increaseViews(id);
    }

    public void likePost(int id) {
        postRepository.likePost(id);
    }
}
