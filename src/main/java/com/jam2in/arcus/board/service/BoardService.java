package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.model.Board;
import com.jam2in.arcus.board.model.Category;
import com.jam2in.arcus.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public void insertBoard(Board board) {
        boardRepository.insert(board);
    }

    public void updateBoard(Board board) {
        boardRepository.update(board);
    }

    public void deleteBoard(int id) {
        boardRepository.delete(id);
    }

    public Board selectOneBoard(int id) {
        return boardRepository.selectOne(id);
    }

    public List<Board> selectAllBoard() {
        return boardRepository.selectAll();
    }

    public List<Board> selectBestRecent() {
        return boardRepository.selectBestRecent();
    }

    public List<Board> selectBestToday() {
        return boardRepository.selectBestToday();
    }

    public void increaseReqRecent(int bid) {
        boardRepository.increaseReqRecent(bid);
    }

    public void increaseReqToday(int bid) {
        boardRepository.increaseReqToday(bid);
    }

    public void resetReqRecent() {
        boardRepository.resetReqRecent();
    }

    public void resetReqToday() {
        boardRepository.resetReqToday();
    }

    public List<Category> boardCategoryAll() {
        return boardRepository.boardCategoryAll();
    }
}
