package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public int create (Comment comment) {
        return commentRepository.insert(comment);
    }

    public int update (Comment comment) {
        return commentRepository.update(comment);
    }

    public int delete (int id) {
        return commentRepository.delete(id);
    }

    public List<Comment> selectAll (int post_id) {
        return commentRepository.selectAll(post_id);
    }

    public Comment selectOne (int id) {
        return commentRepository.selectOne(id);
    }
}
