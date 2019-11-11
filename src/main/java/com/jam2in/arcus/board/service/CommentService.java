package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public int create (Comment comment) {
        return commentRepository.insert(comment);
    }

    public int update (Comment comment) {
        return commentRepository.update(comment);
    }

    public int delete (int id) {
        return commentRepository.delete(id);
    }

    public List<Comment> getAll (int post_id) {
        return commentRepository.selectAll(post_id);
    }

    public Comment get (int id) {
        return commentRepository.selectOne(id);
    }
}
