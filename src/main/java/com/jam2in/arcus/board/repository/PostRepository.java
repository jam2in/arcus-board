package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PostRepository {
    void insert(Post post);
    void update(Post post);
    void delete(int pid);

    Post selectOne(int pid);
    List<Post> selectAll(@Param("bid") int bid, @Param("startList") int startList, @Param("pageSize") int pageSize);
    List<Post> selectCategory(@Param("bid") int bid, @Param("category") int category, @Param("startList") int startList, @Param("pageSize") int pageSize);

    List<Post> selectLikesAll();
    List<Post> selectLikesMonth();
    List<Post> selectLikesToday();
    List<Post> selectViewsAll();
    List<Post> selectViewsMonth();
    List<Post> selectViewsToday();
    List<Post> selectLikesAllBoard(int bid);
    List<Post> selectLikesMonthBoard(int bid);
    List<Post> selectLikesTodayBoard(int bid);
    List<Post> selectViewsAllBoard(int bid);
    List<Post> selectViewsMonthBoard(int bid);
    List<Post> selectViewsTodayBoard(int bid);

    int countPost(int bid);
    int countPostCategory(@Param("bid") int bid, @Param("category") int category);

    void increaseCmt(int pid);
    void decreaseCmt(int pid);

    void increaseViews(int pid);
    void likePost(int pid);
}
