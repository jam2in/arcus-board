package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.repository.PostRepository;
import com.jam2in.arcus.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostRepository postRepository;
    @Autowired
    private PostService postService;

    @RequestMapping(value = "/post/write", method = RequestMethod.POST)
    public String write(@RequestParam("board_id") String board_id, Model model) {
        logger.info("id : {}", board_id);
        model.addAttribute("board_id", board_id);
        return "write";
    }

    @RequestMapping(value = "/post/upload", method = RequestMethod.POST)
    public String upload(@ModelAttribute Post post) {
        logger.info("{}", post.getBoard_id());
        postService.create(post);
        return "redirect:/board/info?id="+post.getBoard_id();
    }

    @RequestMapping(value = "/post/edit", method = RequestMethod.GET)
    public String edit(@RequestParam int id, Model model) {
        logger.info("id : {}", id);
        model.addAttribute("post", postService.get(id));
        return "edit";
    }
    @RequestMapping(value = "/post/update", method = RequestMethod.POST)
    public String update(@ModelAttribute Post post) {
        postService.update(post);
        return "redirect:/post/detail?id="+post.getId();
    }

    @RequestMapping("/post/delete")
    public String delete(@RequestParam int board_id) {
        return "redirect:/board/info?id=" + board_id;
    }

    @RequestMapping("/post/detail")
    public String detail(@RequestParam int id, Model model) {
        logger.info("title : {}", id);
        model.addAttribute("post", postService.get(id));
        return "detail";
    }

    @RequestMapping("/post/comment")
    public String comment() {
        return "redirect:/detail";
    }

}