package com.jam2in.arcus.board.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jam2in.arcus.board.model.Comment;
import com.jam2in.arcus.board.model.Post;
import com.jam2in.arcus.board.service.CommentService;
import com.jam2in.arcus.board.service.PostService;

@RestController
public class DataController {
	@Autowired
	PostService postService;
	@Autowired
	CommentService commentService;

	@RequestMapping(path = "/data/post")
	public String postData() {
		for (int i=0; i<50000; i++) {
			Post post = new Post();
			//int bid = (i%11)+1;
			int bid = 11;
			int uid = (i%1000)+1;
			int num=i+1;
			int category;
			if(bid==1) {
				category=1;
			}
			else {
				category = (i%3)+1;
			}
			String title;
			switch (category) {
				case 1 :
					title = "공지입니다" + num;
					post.setTitle(title);
					break;
				case 2 :
					title = "질문입니다" + num;
					post.setTitle(title);
					break;
				case 3 :
					title = "잡담입니다" + num;
					post.setTitle(title);
					break;
			}
			char[] chars = new char[30000];
			Arrays.fill(chars, 'a');
			String content = new String(chars);

			post.setBid(bid);
			post.setUid(uid);
			post.setCategory(category);
			post.setContent(content);

			postService.insertPost(post);
		}

		return "post insert 완료";
	}

	@RequestMapping(path = "/data/comment")
	public String cmtData() {
		for (int i=0; i<100000; i++) {
			Comment comment = new Comment();
			int pid = (i%20000)+1;
			int uid = (i%1000)+1;
			int num = i+1;
			String content = "댓글입니다" + num;
			comment.setPid(pid);
			comment.setUid(uid);
			comment.setContent(content);
			commentService.insertCmt(comment);
			postService.increaseCmt(comment.getPid());
		}
		return "comment insert 완료";
	}

	@RequestMapping(path = "/data/csv")
	public String csvData() {
		String filePath = "C:/Users/jam2in/Desktop/sy/post_csv.csv";

		File file = null;
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
			for (int i=0; i<50000; i++) {
				int pid=i+1;
				int bid = 1;
				int uid = (i%1000)+1;
				int category = 1;
				String title = "제목입니다" + pid;
				char[] chars = new char[30000];
				Arrays.fill(chars, 'a');
				String content = new String(chars);
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String time = simpleDateFormat.format(timestamp);
				bufferedWriter.write(pid+","+uid+","+bid+","+category+","+title+","+content+",0,0,"+time+","+time+",0");
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "csv 생성 완료";
	}

}
