package com.jam2in.arcus.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.service.BoardService;
import com.jam2in.arcus.board.service.CommentService;
import com.jam2in.arcus.board.service.PostService;

@Controller
public class TestController {
	@Autowired
	BoardService boardService;
	@Autowired
	PostService postService;
	@Autowired
	CommentService commentService;


	@RequestMapping(path = "/test/post")
	public String postDetail(@RequestParam("bid") int bid, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, @ModelAttribute Comment comment, Model model) {
		Post post = postService.selectLatestRandom(bid);
		int pid = post.getPid();
		postService.increaseViews(pid);

		String boardName = boardService.selectOneBoard(bid).getName();
		boardService.increaseReqRecent(bid);
		boardService.increaseReqToday(bid);

		Pagination pagination = new Pagination();
		pagination.setGroupSize(5);
		pagination.setPageSize(10);
		pagination.pageInfo(groupIndex, pageIndex, post.getCmtCnt());

		List<Comment> cmtList = commentService.selectAllCmt(pid, pagination.getStartList()-1, pagination.getPageSize());

		model.addAttribute("post", post);
		model.addAttribute("boardName", boardName);
		model.addAttribute("boardList", boardService.selectAllBoard());
		model.addAttribute("boardCategory", boardService.boardCategoryAll());

		model.addAttribute("cmtList", cmtList);
		model.addAttribute("pagination", pagination);


		return "post/detail";
	}


	@RequestMapping(path = "/test/post/comment")
	public String insertCmt(@RequestParam("bid") int bid, @RequestParam("uid") int uid, @RequestParam("content") String content) {
		Post post = postService.selectLatestRandom(bid);
		int pid = post.getPid();

		Comment comment = new Comment();
		comment.setPid(pid);
		comment.setUid(uid);
		comment.setContent(content);

		commentService.insertCmt(comment);
		postService.increaseCmt(pid);

		boardService.increaseReqRecent(bid);
		boardService.increaseReqToday(bid);

		return "redirect:/post/detail?pid="+comment.getPid();
	}

	@RequestMapping(path = "/test/post/like")
	public String likePost(@RequestParam("bid") int bid){
		Post post = postService.selectLatestRandom(bid);
		int pid = post.getPid();
		postService.likePost(pid);

		return "redirect:/post/detail?pid="+pid;
	}

}
