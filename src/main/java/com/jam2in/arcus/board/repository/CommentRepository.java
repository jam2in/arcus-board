package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository {

    int insert(Comment comment);

    int update(Comment comment);

    int delete(int id);

    List<Comment> selectAll(int post_id);

    List<Comment> selectPage(int post_id, int startList, int pageSize);

    Comment selectOne(int id);

    int countCmt(int post_id);
}
