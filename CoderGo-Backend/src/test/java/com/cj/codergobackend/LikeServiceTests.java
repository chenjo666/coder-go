package com.cj.codergobackend;

import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.pojo.ArticleComment;
import com.cj.codergobackend.pojo.Like;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class LikeServiceTests {
    @Resource
    UserService userService;
    @Resource
    ArticleService articleService;
    @Resource
    FollowService followService;
    @Resource
    LikeService likeService;
    @Resource
    ArticleCommentService articleCommentService;
    @Test
    public void insertData() {
        Random r = new Random();
        List<User> list = userService.list();
        List<Article> list1 = articleService.list();
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
        List<ArticleComment> list1 = articleCommentService.list();
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
