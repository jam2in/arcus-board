package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.repository.PostRepository;
import com.jam2in.arcus.board.service.BoardService;
import com.jam2in.arcus.board.service.CommentService;
import com.jam2in.arcus.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    public static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostRepository postRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

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
        logger.info("[EDIT]post_id : {}", id);
        model.addAttribute(postService.get(id));
        return "postEdit";
    }
    @RequestMapping(value = "/post/update", method = RequestMethod.POST)
    public String update(@ModelAttribute Post post) {
        postService.update(post);
        return "redirect:/post/detail?id="+post.getId();
    }

    @RequestMapping("/post/delete")
    public String delete(@RequestParam int id) {
        int board_id = postService.get(id).getBoard_id();
        postService.delete(id);
        logger.info("[DELETE]post_id : {}", id);
        return "redirect:/board/info?id=" + board_id;
    }

    @RequestMapping("/post/detail")
    public String detail(@RequestParam int id, Model model) {
        /*
        for (int i=0; i < 100; i ++) {
            Comment comment = new Comment();
            comment.setContent("comment"+i);
            comment.setPost_id(328);
            commentService.create(comment);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        Post post = postService.get(id);

        //Comment List Pagination
        Pagination pagination = new Pagination();
        pagination.setPageSize(20);
        pagination.setGroupSize(10);
        pagination.setListCnt(commentService.countCmt(post.getId()));
        pagination.pageInfo(1, 1, pagination.getListCnt());

        logger.info("post detail #{}, pagination: {} {}", id, pagination.getStartRow(), pagination.getEndRow());

        model.addAttribute("comments", new Comment());
        model.addAttribute("post", post);
        model.addAttribute("pagination", pagination);
        return "detail";
    }

    @RequestMapping("/post/comment")
    public String comment() {
        return "redirect:/detail";
    }

}
