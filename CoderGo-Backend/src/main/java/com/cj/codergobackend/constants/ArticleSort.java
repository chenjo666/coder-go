package com.cj.codergobackend.constants;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.codergobackend.pojo.Article;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

public interface ArticleSort {
    String DEFAULT = "default";
    String NEW = "new";
    String HOT = "hot";
    String TOP = "top";

    // es 排序规则
    static void queryOrder(NativeQueryBuilder builder, String order) {
        switch (order) {
            case ArticleSort.DEFAULT -> {
                builder.withSort(Sort.sort(Article.class).descending());
                builder.withSort(Sort.by("score").descending());
            }
            case ArticleSort.NEW -> builder.withSort(Sort.by("createdAt").descending());
            case ArticleSort.HOT -> builder.withSort(Sort.by("score").descending());
            case ArticleSort.TOP -> builder.withSort(Sort.by("isTop","isGem","score").descending());
        }
    }

    // mysql 排序规则
    static void queryOrder(QueryWrapper<Article> query, String order) {
        switch (order) {
            case ArticleSort.DEFAULT, ArticleSort.HOT -> query
                    .orderByDesc("score");
            case ArticleSort.NEW -> query
                    .orderByDesc("created_at");
            case ArticleSort.TOP -> query
                    .orderByDesc("is_top")
                    .orderByDesc("is_gem")
                    .orderByDesc("score");
        }
    }

}
