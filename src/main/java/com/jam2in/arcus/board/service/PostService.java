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
        //postArcus.setPostIno(post);
        //postArcus.setPostContent(post.getId(), post.getContent());
        return postRepository.insert(post);
    }

    public int update(Post post) {
        //postArcus.setPostInfo(post);
        if (!postArcus.setPostContent(post.getId(), post.getContent())) {
        }
        return postRepository.update(post);
    }

    public int delete(int id) {
        //postArcus.delPostInfo(id);
        /*
        if (!postArcus.delPostContent(id)) {
        }
         */
        return postRepository.delete(id);
    }

    public Post get(int id) {
        Post post = null;
        String postContent;
        /* apply arcus memcached */

        if ((post = postArcus.getPostInfo(id)) != null) { // search for b-tree element
            if((postContent = postArcus.getPostContent(id)) == null) {//사실 여기 들어가면 안됨
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
            ///*temporary
            post = postRepository.selectOne(id);

            postArcus.setPostInfo(post);
            //*/
            return postRepository.selectOne(id);
        }
    }

    public List<Post> getPage(int board_id, int startList, int pageSize) {
        List<Post> posts;

        ///*
        if ((posts = postArcus.getPosts(board_id, startList, pageSize)) == null) { //key가 없는지 element가 없는지 구분해서~~
            System.out.println("11111");
            posts = postRepository.selectPage(board_id, startList, pageSize);
        }
        //*/
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
