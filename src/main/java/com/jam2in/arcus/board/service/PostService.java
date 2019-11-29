package com.jam2in.arcus.board.service;

import com.jam2in.arcus.board.PostArcus;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostArcus postArcus;

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
        Post post;
        /* apply arcus memcached */
        if((post=postArcus.getPost(id)) == null) {

            post = postRepository.selectOne(id);
            postArcus.setPost(post);
        }
        return post;

        /* Original
        return postRepository.selectOne(id);
         */
    }

    public List<Post> getPage(int board_id, int startList, int pageSize) {
        return postRepository.selectPage(board_id, startList, pageSize);
    }

    public List<Post> getAll(int board_id) {
        return postRepository.selectAll(board_id);
    }

    public int countPost(int id) {return postRepository.countPost(id);}

}
