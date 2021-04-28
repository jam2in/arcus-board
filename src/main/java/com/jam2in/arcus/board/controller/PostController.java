package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.service.BoardService;
import com.jam2in.arcus.board.service.CommentService;
import com.jam2in.arcus.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/post/write")
    public String writePost(@RequestParam("bid") int bid, @ModelAttribute Post post, Model model) {
        String boardName = boardService.selectOneBoard(bid).getName();

        model.addAttribute("bid", bid);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardList", boardService.selectAllBoard());
        model.addAttribute("boardCategory", boardService.boardCategoryAll());

        if (bid == 1) {
            return "notice/write";
        }
        else {
            return "post/write";
        }
    }

    @RequestMapping( path = "/post/insert")
    public String insertPost(@ModelAttribute Post post) {
        boardService.increaseReqRecent(post.getBid());
        boardService.increaseReqToday(post.getBid());

        postService.insertPost(post);

        if (post.getBid() == 1) {
            return "redirect:/board?bid=1";
        }
        else {
            return "redirect:/board?bid="+post.getBid();
        }
    }

/*    @RequestMapping(path = "/post/detail")
    public String postDetail(@RequestParam("pid") int pid, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, @ModelAttribute Comment comment, Model model) {
        postService.increaseViews(pid);
        Post post = postService.selectOnePost(pid);
        String boardName = boardService.selectOneBoard(post.getBid()).getName();

        model.addAttribute("post", post);
        model.addAttribute("boardName", boardName);

        if (post.getBid() == 0) {
            return "notice/detail";
        }
        else {
            return "post/detail";
        }
    }*/

    @RequestMapping(path = "/post/detail", params = {"pid"})
    public String postDetail(@RequestParam("pid") int pid, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, @ModelAttribute Comment comment, Model model) {
        postService.increaseViews(pid);
        Post post = postService.selectOnePost(pid);
        String boardName = boardService.selectOneBoard(post.getBid()).getName();
        boardService.increaseReqRecent(post.getBid());
        boardService.increaseReqToday(post.getBid());

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

        if (post.getBid() == 1) {
            return "notice/detail";
        }
        else {
            return "post/detail";
        }
    }

    @RequestMapping(path = "/post/detail", params = {"pid", "cid"})
    public String postDetail(@RequestParam("pid") int pid, @RequestParam("cid") int cid, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, Model model) {
        postService.increaseViews(pid);
        Post post = postService.selectOnePost(pid);
        String boardName = boardService.selectOneBoard(post.getBid()).getName();
        boardService.increaseReqRecent(post.getBid());
        boardService.increaseReqToday(post.getBid());

        Pagination pagination = new Pagination();
        pagination.setGroupSize(5);
        pagination.setPageSize(10);
        pagination.pageInfo(groupIndex, pageIndex, post.getCmtCnt());

        List<Comment> cmtList = commentService.selectAllCmt(pid, pagination.getStartList()-1, pagination.getPageSize());
        Comment comment = commentService.selectOneCmt(cid);

        model.addAttribute("post", post);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardList", boardService.selectAllBoard());
        model.addAttribute("boardCategory", boardService.boardCategoryAll());

        model.addAttribute("cmtList", cmtList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("cid", cid);
        model.addAttribute("comment", comment);

        if (post.getBid() == 1) {
            return "notice/detail";
        }
        else {
            return "post/detail";
        }

    }

    @RequestMapping(path = "/post/edit")
    public String editPost(@RequestParam("pid") int pid, Model model) {
        Post post = postService.selectOnePost(pid);
        String boardName = boardService.selectOneBoard(post.getBid()).getName();

        model.addAttribute("post", post);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardList", boardService.selectAllBoard());
        model.addAttribute("boardCategory", boardService.boardCategoryAll());
        model.addAttribute("postCategory", postService.postCategoryAll());

        if (post.getBid() == 1) {
            return "notice/edit";
        }
        else {
            return "post/edit";
        }
    }

    @RequestMapping(path = "/post/update")
    public String updatePost(@ModelAttribute Post post) {
        boardService.increaseReqRecent(post.getBid());
        boardService.increaseReqToday(post.getBid());

        postService.updatePost(post);

        return "redirect:/post/detail?pid="+post.getPid();
    }

    @RequestMapping(path = "/post/delete")
    public String deletePost(@RequestParam("pid") int pid) {
        int bid = postService.selectOnePost(pid).getBid();
        boardService.increaseReqRecent(bid);
        boardService.increaseReqToday(bid);

        postService.deletePost(pid);

        if (bid == 1) {
            return "redirect:/board?bid=1";
        }
        else {
            return "redirect:/board?bid="+bid;
        }
    }


    @RequestMapping(path = "/post/like")
    public String likePost(@RequestParam("pid") int pid) {
        postService.likePost(pid);
        return "redirect:/post/detail?pid="+pid;
    }

/*    @ResponseBody
    @RequestMapping(path = "/post/like", method = RequestMethod.POST, produces = "application/json; charset=utf8")
    public ResponseEntity likePost(@RequestBody HashMap<String,Integer> hashMap) {
        int pid = hashMap.get("pid");
        postService.likePost(pid);

        int likes = postService.selectOnePost(pid).getLikes();

        return new ResponseEntity(likes, HttpStatus.OK);
    }*/

}
