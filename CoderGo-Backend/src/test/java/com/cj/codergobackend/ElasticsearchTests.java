package com.cj.codergobackend;

import com.alibaba.fastjson2.JSON;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.script.Script;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class ElasticsearchTests {

    @Resource
    private ElasticsearchTemplate template;
    @Resource
    private ElasticsearchOperations operations;
    @Resource
    private UserService userService;
    @Resource
    private ArticleService articleService;

    @Test
    public void testCreateIndex() {
        List<Article> list = articleService.list();
        operations.save(list);
    }

    @Test
    public void addData() {
        String[] types = new String[]{"discussion", "help", "sharing", "tutorial",
                "emotional", "news", "review", "survey"};

        List<String> additionalTags = Arrays.asList(
                "Docker", "Kubernetes", "AWS", "Git", "MongoDB", "Python", "JavaScript", "React",
                "Angular", "Node.js", "Express.js", "HTML", "CSS", "RESTful", "Microservices", "GraphQL",
                "OAuth", "JWT", "Spring Framework", "Hibernate", "ORM", "Jenkins", "Ansible", "CI/CD",
                "Agile", "Scrum", "DevOps", "Machine Learning", "Deep Learning", "Neural Networks",
                "Natural Language Processing", "Computer Vision", "Big Data", "Hadoop", "Spark",
                "Cassandra", "Elasticsearch", "RabbitMQ", "ActiveMQ", "Websockets", "React Native",
                "Ionic", "Android", "iOS", "Swift", "Kotlin", "C#", ".NET", "Unity", "Game Development"
        );

        Random random = new Random();
        List<Article> p = articleService.list();
        // 1. 构建作者
        List<User> users = userService.list();
        // 2. 构建标题
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            Article article = new Article();
            article.setUserId(users.get(random.nextInt(users.size())).getId());
            article.setTitle(p.get(random.nextInt(p.size())).getTitle());
            article.setContent(p.get(random.nextInt(p.size())).getContent());
            article.setType(types[random.nextInt(8)]);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            article.setCreatedAt(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

            article.setIsGem(random.nextInt(2));
            article.setIsTop(random.nextInt(2));
            article.setTotalReply(0L);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < random.nextInt(5); j++) {
                sb.append(additionalTags.get(random.nextInt(40)));
            }
            article.setTags(sb.toString());
            article.setScore(random.nextDouble(200));

            articles.add(article);
        }

//        postService.saveBatch(posts);
        operations.save(articles);
    }

    @Test
    public void deleteIndex() {
        operations.indexOps(Article.class).delete();
    }

    @Test
    public void copyDataFromMysql() {
        List<Article> list = articleService.list();
        operations.save(list);
    }

    @Test
    public void resetData() {
        SearchHits<Article> search = operations.search(NativeQuery.builder().build(), Article.class);
        List<Article> articles = new ArrayList<>();
        for (SearchHit<Article> postSearchHit : search) {
            Article p = postSearchHit.getContent();
            p.setTitle("java 祖师爷");
            p.setContent("java 祖师");
            p.setScore(0.0);
            articles.add(p);
        }
        for (Article article : articles) {
            operations.save(article);
        }
        System.out.println(search.getTotalHits());
    }

    @Test
    public void testSearchService() {
        List<HighlightField> list = new ArrayList<>();
        list.add(new HighlightField("title"));
        list.add(new HighlightField("content"));
        Highlight highlight = new Highlight(list);
        HighlightQuery highlightQuery = new HighlightQuery(highlight, Article.class);

        Query query = NativeQuery.builder()
                .withFilter(q -> q.term(t -> t.field("type").value("discussion")))
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title", "content")
                                .query("java")
                        ))
                .withHighlightQuery(highlightQuery)
                .withSort(Sort.by("publishTime").descending())
                .withPageable(PageRequest.of(0, 2))
                .build();


        SearchHits<Article> searchHits = template.search(query, Article.class);
        for (SearchHit<Article> searchHit : searchHits) {
            System.out.println(searchHit);
        }
    }

    @Test
    public void testSuggester() {

    }

    @Test
    public void testInc() {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.term(m -> m.field("id").value(1684145454774566914L)))
                .build();
        SearchHit<Article> search = operations.searchOne(query, Article.class);
        assert search != null;
        Article article = search.getContent();
        article.setTotalReply(article.getTotalReply() + 1);
        operations.update(article);
    }
}
