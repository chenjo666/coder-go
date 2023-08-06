package com.cj.codergobackend.constants;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.codergobackend.pojo.ArticleComment;

public interface ArticleCommentSort {
    String DEFAULT = "default";
    String NEW = "new";

    static void querySort(QueryWrapper<ArticleComment> query, String order) {
        switch (order) {
            case DEFAULT -> query.orderByDesc("score");
            case NEW -> query.orderByDesc("created_at");
        }
    }
}
