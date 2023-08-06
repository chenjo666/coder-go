package com.cj.codergobackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.codergobackend.pojo.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}
