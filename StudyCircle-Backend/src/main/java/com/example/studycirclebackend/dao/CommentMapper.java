package com.example.studycirclebackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studycirclebackend.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
