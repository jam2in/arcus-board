package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.repository.CommentRepository;
import com.jam2in.arcus.board.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommentController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/cmt/add", method = RequestMethod.POST)
    public String add(@ModelAttribute Comment comment, Model model) {
        logger.info("comment : {}, post_id: {}", comment.getContent(), comment.getPost_id());
        commentService.create(comment);
        model.addAttribute("comments", commentService.getAll(comment.getPost_id()));
        return "redirect:/post/detail?id=" + comment.getPost_id();
    }
}
