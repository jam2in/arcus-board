package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository {

    int insert(Board board);

    int update(Board board);

    int delete(int id);

    Board selectOne(int id);

    List<Board> selectAll();

}