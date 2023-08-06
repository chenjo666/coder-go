package com.cj.codergobackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.codergobackend.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
