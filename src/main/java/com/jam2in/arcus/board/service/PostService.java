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
        //최신 글 N개를 캐싱해야하므로 생성하자마자 캐싱
        int result = postRepository.insert(post);
        postArcus.setPostInfo(get(post.getId(), post.getBoard_id()));
        return result;
    }

    public int update(Post post) {
        if (postArcus.updatePostInfo(post)) {
            postArcus.setPostContent(post.getId(), post.getContent());
        }
        return postRepository.update(post);
    }

    public int delete(int id, int board_id) {
        postArcus.delPostInfo(id, board_id);
        return postRepository.delete(id);
    }

    public Post get(int id, int board_id) {
        increaseViews(id);
        Post post = null;
        String postContent;
        /* apply arcus memcached */

        // search for b-tree element
        if ((post = postArcus.getPostInfo(id, board_id)) != null) {
            if((postContent = postArcus.getPostContent(id)) == null) {
                post = postRepository.selectOne(id);
                postArcus.setPostContent(id, post.getContent());
                return post;
            }
            else {
                post.setContent(postContent);
                return post;
            }
        }
        else {
            return postRepository.selectOne(id);
        }
    }

    public List<Post> getPage(int board_id, int startList, int pageSize) {
        List<Post> posts;


        if ((posts = postArcus.getPosts(board_id, startList, pageSize)) == null) {
            posts = postRepository.selectPage(board_id, startList, pageSize);
        }

        return posts;
    }

    public List<Post> getAll(int board_id) {
        return postRepository.selectAll(board_id);
    }

    public int countPost(int id) {return postRepository.countPost(id);}

    public int increaseViews(int post_id) {
        return  postRepository.increaseViews(post_id);
    }
}
