package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.Application;
import com.jam2in.arcus.board.BoardArcus;
import com.jam2in.arcus.board.model.Board;
import com.jam2in.arcus.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardArcus boardArcus;

    public int create(Board board) {
        return boardRepository.insert(board);
    }

    public int update(int id, Board board) {
        board.setId(id);
        return update(board);
    }

    public int update(Board board) {
        return boardRepository.update(board);
    }

    public int remove(Board board) {
        if (Application.CACHE) {
            boardArcus.bopDelBoard(board.getId());
        }
        return boardRepository.delete(board.getId());
    }

    public Board get(int id) {
        return boardRepository.selectOne(id);
    }

    public List<Board> getAll() {
        return boardRepository.selectAll();
    }

}
