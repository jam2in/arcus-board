package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.Application;
import com.jam2in.arcus.board.CommentArcus;
import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.repository.CommentRepository;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.javassist.tools.rmi.AppletServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentArcus commentArcus;

    public int create (Comment comment) {
        int result = commentRepository.insert(comment);
        if (Application.CACHE) {
            commentArcus.setComment(get(comment.getId()));
        }
        return result;
    }

    public int update (Comment comment) {
        if (Application.CACHE) {
            commentArcus.updateComment(comment);
        }
        return commentRepository.update(comment);
    }

    public int delete (int id, int board_id) {
        if (Application.CACHE) {
            commentArcus.deleteComment(id, board_id);
        }
        return commentRepository.delete(id);
    }

    public List<Comment> getAll (int post_id) {
        return commentRepository.selectAll(post_id);
    }

    public List<Comment> getPage (int post_id, int startList, int pageSize) {
        List<Comment> comments;

        if (Application.CACHE) {
            if ((comments = commentArcus.getComments(post_id, startList, pageSize)) == null && startList != 0) {
                comments = commentRepository.selectPage(post_id, startList, pageSize);
            }
        }
        else {
            comments = commentRepository.selectPage(post_id, startList, pageSize);
        }
        return comments;
    }

    public int countCmt(int post_id) {
        return commentRepository.countCmt(post_id);
    }
    public Comment get (int id) {
        return commentRepository.selectOne(id);
    }
}
