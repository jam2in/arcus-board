package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository {

    int insert(Post post);

    int update(Post post);

    int delete(int id);

    Post selectOne(int id);

    List<Post> selectPage(int board_id, Pagination pagination);

    List<Post> selectAll(int board_id);

    int countPost(int id);
}
