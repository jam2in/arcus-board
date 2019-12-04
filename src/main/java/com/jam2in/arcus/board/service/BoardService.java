package com.jam2in.arcus.board.service;

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
        boardArcus.bopCreateBoard(board.getId());
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
        return boardRepository.delete(board.getId());
    }

    public Board get(int id) {
        //temporary
        //boardArcus.bopCreateBoard(id);
        return boardRepository.selectOne(id);
    }

    public List<Board> getAll() {
        return boardRepository.selectAll();
    }

}
