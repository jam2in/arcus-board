package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository {

    int insert(Comment comment);

    int update(Comment comment);

    int delete(int id);

    List<Comment> selectAll(int post_id);

    Comment selectOne(int id);
}
