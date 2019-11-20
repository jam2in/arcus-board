package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
import com.jam2in.arcus.board.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    CommentService commentService;

    @ResponseBody
    @RequestMapping(path = "cmt/add", method = RequestMethod.POST)
    public String add(@RequestBody Comment comment) {
        if (commentService.create(comment) == 0) {
            //Response HTTP Error (CONFLICT)
        }
        logger.info("[ADDED]comment : {}, post_id: {}", comment.getContent(), comment.getPost_id());
        return "";
    }

    @ResponseBody
    @RequestMapping(path = "cmt/update")
    public String update(@RequestBody Comment comment, Model model) {
        if (commentService.update(comment) == 0) {
            //Response HTTP Error (CONFLICT)
        }
        logger.info("[EDIT]comment : {}", comment.getContent());
        //return "redirect:/post/detail?id=" + comment.getPost_id();
        return "";
    }

    @ResponseBody
    @RequestMapping(path = "cmt/delete")
    public String delete(@RequestParam int id) {
        Comment comment = commentService.get(id);
        if (commentService.delete(id) == 0) {
            //Response HTTP Error (CONFLICT)
        }
        logger.info("[DELETED]");
        return "";
    }

    @ResponseBody
    @RequestMapping(path = "cmt/list", method = RequestMethod.GET)
    public List<Comment> getCommentList(@RequestParam int post_id) {
        logger.info("ajax id : " + post_id);
        return commentService.getAll(post_id);
    }

}
