package com.jam2in.arcus.board.repository;

import com.jam2in.arcus.board.model.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository {

    public void insert(Board board);

    public void update(Board board);

    public void delete(String boardId);

    public Board select(String boardId);

    public List<Board> selectAll();

}