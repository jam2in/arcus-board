package com.jam2in.arcus.board.controller;

import com.jam2in.arcus.board.model.Board;
import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.service.BoardService;
import com.jam2in.arcus.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    @Scheduled(cron = "0 0 0/3 * * *")
    public void resetReqRecent() {
        boardService.resetReqRecent();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetReqToday() {
        boardService.resetReqToday();
    }

    @RequestMapping(path = "/")
    public String home(@RequestParam(defaultValue = "0") int likesPeriod, @RequestParam(defaultValue = "0") int viewsPeriod, @RequestParam(defaultValue = "0") int bestPeriod, Model model) {
        List<Post> noticeList = postService.selectCategory(1, 1,0, 3);

        List<Post> bestLikes;
        List<Post> bestViews;
        List<Board> bestBoard;

        switch (likesPeriod) {
            case 2:
                bestLikes = postService.selectLikesAll();
                break;
            case 1:
                bestLikes = postService.selectLikesMonth();
                break;
            case 0:
                bestLikes = postService.selectLikesToday();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        switch (viewsPeriod) {
            case 2:
                bestViews = postService.selectViewsAll();
                break;
            case 1:
                bestViews = postService.selectViewsMonth();
                break;
            case 0:
                bestViews = postService.selectViewsToday();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        switch (bestPeriod) {
            case 0:
                bestBoard = boardService.selectBestRecent();
                break;
            case 1:
                bestBoard = boardService.selectBestToday();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + bestPeriod);
        }

        model.addAttribute("likesPeriod", likesPeriod);
        model.addAttribute("viewsPeriod", viewsPeriod);
        model.addAttribute("bestPeriod", bestPeriod);
        model.addAttribute("bestLikes", bestLikes);
        model.addAttribute("bestViews", bestViews);
        model.addAttribute("bestBoard", bestBoard);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("boardList", boardService.selectAllBoard());

        return "home";
    }

    @RequestMapping(path = "/board", params = {"bid"})
    public String postList(@RequestParam("bid") int bid, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, @RequestParam(defaultValue = "0") int likesPeriod, @RequestParam(defaultValue = "0") int viewsPeriod, Model model) {
        Board board = boardService.selectOneBoard(bid);
        String boardName = board.getName();
        boardService.increaseReqRecent(bid);
        boardService.increaseReqToday(bid);

        int listCnt = postService.countPost(bid);

        Pagination pagination = new Pagination();
        pagination.setGroupSize(10);
        pagination.setPageSize(20);
        pagination.pageInfo(groupIndex, pageIndex, listCnt);
        model.addAttribute("pagination", pagination);

        List<Post> postList = postService.selectAll(bid, pagination.getStartList()-1, pagination.getPageSize());
        model.addAttribute("postList", postList);

        List<Post> noticeList = postService.selectCategory(bid, 1, 0, 2);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("bid", bid);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardList", boardService.selectAllBoard());

        List<Post> bestLikes;
        List<Post> bestViews;

        switch (likesPeriod) {
            case 0:
                bestLikes = postService.selectLikesTodayBoard(bid);
                break;
            case 1:
                bestLikes = postService.selectLikesMonthBoard(bid);
                break;
            case 2:
                bestLikes = postService.selectLikesAllBoard(bid);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        switch (viewsPeriod) {
            case 0:
                bestViews = postService.selectViewsTodayBoard(bid);
                break;
            case 1:
                bestViews = postService.selectViewsMonthBoard(bid);
                break;
            case 2:
                bestViews = postService.selectViewsAllBoard(bid);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        model.addAttribute("likesPeriod", likesPeriod);
        model.addAttribute("viewsPeriod", viewsPeriod);
        model.addAttribute("bestLikes", bestLikes);
        model.addAttribute("bestViews", bestViews);

        if (bid == 1) {
            return "notice/board";
        }
        else {
            return "post/board";
        }
    }

    @RequestMapping(path = "/board", params = {"bid", "category"})
    public String postList(@RequestParam("bid") int bid, @RequestParam("category") int category, @RequestParam(defaultValue = "1") int groupIndex, @RequestParam(defaultValue = "1") int pageIndex, @RequestParam(defaultValue = "0") int likesPeriod, @RequestParam(defaultValue = "0") int viewsPeriod, Model model) {
        Board board = boardService.selectOneBoard(bid);
        String boardName = board.getName();
        boardService.increaseReqRecent(bid);
        boardService.increaseReqToday(bid);

        int listCnt = postService.countPostCategory(bid, category);

        Pagination pagination = new Pagination();
        pagination.setGroupSize(10);
        pagination.setPageSize(20);
        pagination.pageInfo(groupIndex, pageIndex, listCnt);

        List<Post> postList = postService.selectCategory(bid, category, pagination.getStartList()-1, pagination.getPageSize());
        List<Post> noticeList = postService.selectCategory(bid, 1, 0, 2);

        model.addAttribute("postList", postList);
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("bid", bid);
        model.addAttribute("category", category);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardList", boardService.selectAllBoard());

        List<Post> bestLikes;
        List<Post> bestViews;

        switch (likesPeriod) {
            case 0:
                bestLikes = postService.selectLikesTodayBoard(bid);
                break;
            case 1:
                bestLikes = postService.selectLikesMonthBoard(bid);
                break;
            case 2:
                bestLikes = postService.selectLikesAllBoard(bid);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        switch (viewsPeriod) {
            case 0:
                bestViews = postService.selectViewsTodayBoard(bid);
                break;
            case 1:
                bestViews = postService.selectViewsMonthBoard(bid);
                break;
            case 2:
                bestViews = postService.selectViewsAllBoard(bid);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + likesPeriod);
        }

        model.addAttribute("likesPeriod", likesPeriod);
        model.addAttribute("viewsPeriod", viewsPeriod);
        model.addAttribute("bestLikes", bestLikes);
        model.addAttribute("bestViews", bestViews);

        return "post/board";
    }


}