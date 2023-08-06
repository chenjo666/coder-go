package com.cj.codergobackend;

import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.pojo.ArticleComment;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class ArticleCommentServiceTests {

    @Resource
    ArticleCommentService articleCommentService;
    @Resource
    ArticleService articleService;
    @Resource
    UserService userService;



    // 为每个文章添加一些评论
    @Test
    public void insertData() {
        List<Article> list = articleService.list();
        List<User> users = userService.list();
        Random random = new Random();

        for (Article p : list) {
            List<ArticleComment> articleComments = new ArrayList<>();
            for (int i = 0; i < random.nextInt(100); i++) {
                ArticleComment articleComment = new ArticleComment();
                articleComment.setObjectType("post");
                articleComment.setObjectId(p.getId());
                articleComment.setUserId(users.get(random.nextInt(users.size())).getId());
                articleComment.setContent(generateEnglishEssay(10, 50));

                // 随机生成时间
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
                articleComment.setCreatedAt(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

                articleComments.add(articleComment);
            }
            articleCommentService.saveBatch(articleComments);
        }
    }

    // 为每个评论添加条评论
    @Test
    public void insertData2() throws InterruptedException {
        List<ArticleComment> list = articleCommentService.list();
        List<User> list1 = userService.list();

        Random random = new Random();

        for (ArticleComment articleComment : list) {
            User user = userService.getById(articleComment.getUserId());
            ArticleComment c = new ArticleComment();
            c.setObjectType("comment");
            c.setObjectId(articleComment.getId());
            c.setUserId(list1.get(random.nextInt(list1.size())).getId());
            c.setContent("@" + user.getUsername() + " " + generateEnglishEssay(10, 50));

            // 随机生成时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            c.setCreatedAt(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

            articleCommentService.saveOrUpdate(c);
        }
    }

    // 为每个评论随机一些分数
    @Test
    public void randomScore() {
        Random r = new Random();
        List<ArticleComment> list = articleCommentService.list();
        for (ArticleComment articleComment : list) {
            articleComment.setScore(r.nextInt(1000));
        }
        articleCommentService.updateBatchById(list);
    }

    // 更新文章评论类型
    @Test
    public void setNewObjectType() {
        List<ArticleComment> list = articleCommentService.list();
        for (ArticleComment articleComment : list) {
            articleComment.setObjectType(articleComment.getObjectType().equals("2") ? "comment" : "post");
        }
        articleCommentService.updateBatchById(list);
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
        System.out.println(articleCommentService.getArticleIdByCommentId(1677292915944931330L));
    }
}
