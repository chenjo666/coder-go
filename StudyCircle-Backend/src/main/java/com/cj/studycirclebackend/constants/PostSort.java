package com.cj.studycirclebackend.constants;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.studycirclebackend.pojo.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

public interface PostSort {
    String DEFAULT = "default";
    String NEW = "new";
    String HOT = "hot";
    String TOP = "top";

    // es 排序规则
    static void queryOrder(NativeQueryBuilder builder, String order) {
        switch (order) {
            case PostSort.DEFAULT -> {
                builder.withSort(Sort.sort(Post.class).descending());
                builder.withSort(Sort.by("score").descending());
            }
            case PostSort.NEW -> builder.withSort(Sort.by("publishTime").descending());
            case PostSort.HOT -> builder.withSort(Sort.by("score").descending());
            case PostSort.TOP -> builder.withSort(Sort.by("isTop","isGem","score").descending());
        }
    }

    // mysql 排序规则
    static void queryOrder(QueryWrapper<Post> query, String order) {
        switch (order) {
            case PostSort.DEFAULT, PostSort.HOT -> query
                    .orderByDesc("score");
            case PostSort.NEW -> query
                    .orderByDesc("publish_time");
            case PostSort.TOP -> query
                    .orderByDesc("is_top")
                    .orderByDesc("is_gem")
                    .orderByDesc("score");
        }
    }

}
