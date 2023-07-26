package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.constants.PostSort;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.PostService;
import com.cj.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class ElasticsearchTests {

    @Resource
    private ElasticsearchTemplate template;
    @Resource
    private ElasticsearchOperations operations;
    @Resource
    private UserService userService;
@Resource
private PostService postService;
    @Test
    public void testCreateIndex() {
        List<Post> list = postService.list();
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
        List<Post> p = postService.list();
        // 1. 构建作者
        List<User> users = userService.list();
        // 2. 构建标题
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Post post = new Post();
            post.setUserId(users.get(random.nextInt(users.size())).getId());
            post.setTitle(p.get(random.nextInt(p.size())).getTitle());
            post.setContent(p.get(random.nextInt(p.size())).getContent());
            post.setType(types[random.nextInt(8)]);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            post.setPublishTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));

            post.setIsGem(random.nextInt(2));
            post.setIsTop(random.nextInt(2));
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < random.nextInt(5); j++) {
                sb.append(additionalTags.get(random.nextInt(40)));
            }
            post.setTags(sb.toString());
            post.setScore(0.0);

            posts.add(post);
        }

        postService.saveBatch(posts);
        operations.save(posts);
    }

    @Test
    public void resetData() {
        SearchHits<Post> search = operations.search(NativeQuery.builder().build(), Post.class);
        List<Post> posts = new ArrayList<>();
        for (SearchHit<Post> postSearchHit : search) {
            Post p = postSearchHit.getContent();
            p.setTitle("java 祖师爷");
            p.setContent("java 祖师");
            p.setScore(0.0);
            posts.add(p);
        }
        for (Post post : posts) {
            operations.save(post);
        }
        System.out.println(search.getTotalHits());
    }

    @Test
    public void testSearchService() {
        List<HighlightField> list = new ArrayList<>();
        list.add(new HighlightField("title"));
        list.add(new HighlightField("content"));
        Highlight highlight = new Highlight(list);
        HighlightQuery highlightQuery = new HighlightQuery(highlight, Post.class);

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


        SearchHits<Post> searchHits = template.search(query, Post.class);
        for (SearchHit<Post> searchHit : searchHits) {
            System.out.println(searchHit);
        }
    }

    @Test
    public void testSuggester() {

    }
}
