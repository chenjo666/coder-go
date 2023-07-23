package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.pojo.Comment;
import com.cj.studycirclebackend.pojo.Like;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class LikeServiceTests {
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    @Resource
    FollowService followService;
    @Resource
    LikeService likeService;
    @Resource
    CommentService commentService;
    @Test
    public void insertData() {
        Random r = new Random();
        List<User> list = userService.list();
        List<Post> list1 = postService.list();
        List<Like> likes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Set<Integer> set = new HashSet<>();
            for (int j = 0; j < 50; j++) {
                Like like = new Like();
                like.setUserId(list.get(i).getId());
                like.setObjectType("post");
                int i1 = r.nextInt(list1.size());
                like.setObjectId(list1.get(i1).getId());
                if (set.contains(i1)) {
                    continue;
                }
                set.add(i1);
                likes.add(like);
            }
        }
        likeService.saveOrUpdateBatch(likes);
    }
    @Test
    public void insertData2() {
        Random r = new Random();
        List<User> list = userService.list();
        List<Comment> list1 = commentService.list();
        List<Like> likes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Set<Integer> set = new HashSet<>();
            for (int j = 0; j < 100; j++) {
                Like like = new Like();
                like.setUserId(list.get(i).getId());
                like.setObjectType("comment");
                int i1 = r.nextInt(list1.size());
                like.setObjectId(list1.get(i1).getId());
                if (set.contains(i1)) {
                    continue;
                }
                set.add(i1);
                likes.add(like);
            }
        }
        likeService.saveOrUpdateBatch(likes);

    }
}