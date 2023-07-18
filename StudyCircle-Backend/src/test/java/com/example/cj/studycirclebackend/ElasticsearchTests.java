package com.example.cj.studycirclebackend;

import com.example.studycirclebackend.StudyCircleBackendApplication;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.service.PostService;
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

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class ElasticsearchTests {

    @Resource
    private ElasticsearchTemplate template;
    @Resource
    private ElasticsearchOperations operations;

@Resource
private PostService postService;
    @Test
    public void testCreateIndex() {
        operations.save(postService.list());
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
}
