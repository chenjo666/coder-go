package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.pojo.Comment;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.CommentService;
import com.cj.studycirclebackend.service.PostService;
import com.cj.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class CommentServiceTests {

    @Resource
    CommentService commentService;
    @Resource
    PostService postService;
    @Resource
    UserService userService;



    // 为每个帖子添加一些评论
    @Test
    public void insertData() {
        List<Post> list = postService.list();
        List<User> users = userService.list();
        Random random = new Random();

        for (Post p : list) {
            List<Comment> comments = new ArrayList<>();
            for (int i = 0; i < random.nextInt(100); i++) {
                Comment comment = new Comment();
                comment.setObjectType("post");
                comment.setObjectId(p.getId());
                comment.setUserId(users.get(random.nextInt(users.size())).getId());
                comment.setContent(generateEnglishEssay(10, 50));

                // 随机生成时间
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
                comment.setCommentTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

                comments.add(comment);
            }
            commentService.saveBatch(comments);
        }
    }

    // 为每个评论添加条评论
    @Test
    public void insertData2() throws InterruptedException {
        List<Comment> list = commentService.list();
        List<User> list1 = userService.list();

        Random random = new Random();

        for (Comment comment : list) {
            User user = userService.getById(comment.getUserId());
            Comment c = new Comment();
            c.setObjectType("comment");
            c.setObjectId(comment.getId());
            c.setUserId(list1.get(random.nextInt(list1.size())).getId());
            c.setContent("@" + user.getUsername() + " " + generateEnglishEssay(10, 50));

            // 随机生成时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            c.setCommentTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

            commentService.saveOrUpdate(c);
        }
    }

    // 为每个评论随机一些分数
    @Test
    public void randomScore() {
        Random r = new Random();
        List<Comment> list = commentService.list();
        for (Comment comment : list) {
            comment.setScore(r.nextInt(1000));
        }
        commentService.updateBatchById(list);
    }

    // 更新帖子评论类型
    @Test
    public void setNewObjectType() {
        List<Comment> list = commentService.list();
        for (Comment comment : list) {
            comment.setObjectType(comment.getObjectType().equals("2") ? "comment" : "post");
        }
        commentService.updateBatchById(list);
    }

    private static String generateEnglishEssay(int minWords, int maxWords) {
        int wordCount = getRandomNumberInRange(minWords, maxWords);
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < wordCount; i++) {
            char ch = (char) (random.nextInt(26) + 'a');
            sb.append(ch);
        }

        return sb.toString();
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    @Test
    public void testGetPostByCommentId() {
        System.out.println(commentService.getPostIdByCommentId(1677292915944931330L));
    }
}
