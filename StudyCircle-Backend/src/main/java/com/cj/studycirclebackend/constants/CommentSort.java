package com.cj.studycirclebackend.constants;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.studycirclebackend.pojo.Comment;

public interface CommentSort {
    String DEFAULT = "default";
    String NEW = "new";

    static void querySort(QueryWrapper<Comment> query, String order) {
        switch (order) {
            case DEFAULT -> query.orderByDesc("score");
            case NEW -> query.orderByDesc("comment_time");
        }
    }
}
