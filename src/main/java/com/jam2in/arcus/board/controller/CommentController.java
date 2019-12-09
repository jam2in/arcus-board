package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
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

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    CommentService commentService;

    @ResponseBody
    @RequestMapping(path = "cmt/add", method = RequestMethod.POST)
    public void add(@RequestBody Comment comment) {
        if (commentService.create(comment) == 0) {
            //Response HTTP Error (CONFLICT)
        }
        logger.info("[ADDED]comment : {}, post_id: {}", comment.getContent(), comment.getPost_id());
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
    public void delete(@RequestParam int id, @RequestParam int post_id) {
        Comment comment = commentService.get(id);
        if (commentService.delete(id, post_id) == 0) {
            //Response HTTP Error (CONFLICT)
        }
        logger.info("[DELETED]comment : {}", id);
    }

    @ResponseBody
    @RequestMapping(path = "cmt", method = RequestMethod.GET)
    public List<Comment> getComment(@RequestParam int post_id,
                                    @RequestParam (required = false, defaultValue = "1")int groupIndex,
                                    @RequestParam (required = false, defaultValue = "1")int pageIndex) {
        Pagination pagination = new Pagination();
        //pagination.setPageSize(20);
        pagination.setGroupSize(10);
        pagination.setListCnt(commentService.countCmt(post_id));
        pagination.pageInfo(groupIndex, pageIndex, pagination.getListCnt());

        return commentService.getPage(post_id, pagination.getStartList()-1, pagination.getPageSize());
    }

    @ResponseBody
    @RequestMapping(path = "cmt/pagination", method = RequestMethod.GET)
    public Pagination getPagination(@RequestParam int post_id,
                                    @RequestParam int pageIndex,
                                    @RequestParam int groupIndex) {
        Pagination pagination = new Pagination();
        //pagination.setPageSize(20);
        pagination.setGroupSize(10);
        pagination.setListCnt(commentService.countCmt(post_id));
        pagination.pageInfo(groupIndex, pageIndex, pagination.getListCnt());
        return pagination;
    }
}
