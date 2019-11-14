package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.model.Pagination;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.awt.print.Pageable;
import java.util.List;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public int create(Post post) {
        return postRepository.insert(post);
    }

    public int update(Post post) {
        return postRepository.update(post);
    }

    public int delete(int id) {
        return postRepository.delete(id);
    }

    public Post get(int id) {
        return postRepository.selectOne(id);
    }

    public List<Post> getPage(int board_id, Pagination pagination) {
        return postRepository.selectPage(board_id, pagination);
    }

    public List<Post> getAll(int board_id) {
        return postRepository.selectAll(board_id);
    }

    public int countPost(int id) {return postRepository.countPost(id);}

}
