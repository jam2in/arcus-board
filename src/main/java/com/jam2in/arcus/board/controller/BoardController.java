package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Board;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.service.BoardService;
import com.jam2in.arcus.board.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;
    @Autowired
    private PostService postService;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("id", 1);
        model.addAttribute("name", "hello");
        return "home";
    }

    //************************************************************************************************
    // REST API (application/json)
    //************************************************************************************************
    /*
    @RequestMapping(path = "/board", method = RequestMethod.POST)
    @ResponseBody
    public Board createRestAPI(@RequestBody Board board) {
        if (boardService.create(board) == 0) {
            // TODO: Response HTTP Error (CONFLICT)
        }

        return board;
    }

    @RequestMapping(path = "/board/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Board updateRestAPI(@PathVariable int id, @RequestBody Board board) {
        if (boardService.update(id, board) == 0) {
            // TODO: Response HTTP Error (NOT_FOUND)
        }

        return board;
    }

    @RequestMapping(path = "/board/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Board getRestAPI(@PathVariable int id) {
        Board board = boardService.get(id);
        if (board == null) {
            // TODO: Response HTTP Error (NOT_FOUND)
        }

        return board;
    }

    @RequestMapping(path = "/board", method = RequestMethod.GET)
    @ResponseBody
    public List<Board> getAllRestAPI() {
        return boardService.getAll();
    }
    */


    //************************************************************************************************
    // FORM (form-data, x-www-form-urlencoded)
    //************************************************************************************************
    @RequestMapping(path = "/board/create", method = RequestMethod.POST)
    public String create(@ModelAttribute Board board) {
        LOGGER.info("CREATE BOARD");
        boardService.create(board);
        return "redirect:/board";
    }

    @RequestMapping(path = "/board/update", method = RequestMethod.POST)
    public String update(@ModelAttribute Board board) {
        LOGGER.info("UPDATE BOARD");
        boardService.update(board);
        return "board";
    }

    @RequestMapping(path = "/board/remove", method = RequestMethod.POST)
    public String delete(@ModelAttribute Board board) {
        LOGGER.info("REMOVE BOARD");
        boardService.remove(board);
        return "redirect:/board";
    }

    /* ORIGINAL
    @RequestMapping(path = "/board/info", method = RequestMethod.GET)
    public String get(@RequestParam int id) {
        LOGGER.info("GET BOARD");
        boardService.get(id);
        return "board";
    }
     */
    @RequestMapping(path = "/board/info", method = RequestMethod.GET)
    public String get(@RequestParam int id, Model model) {
        LOGGER.info("GET BOARD # {}", id);
        model.addAttribute("board_id", id);
        model.addAttribute("board_name", boardService.get(id).getName());
        model.addAttribute("posts", postService.getAll(id));
        return "list";
    }

    @RequestMapping(path = "/board", method = RequestMethod.GET)
    public String getAll(Model model) {
        LOGGER.info("GET ALL BOARD");
        model.addAttribute("boards", boardService.getAll());
        return "board";
    }

}
