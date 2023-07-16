package com.example.studycirclebackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studycirclebackend.pojo.Conversation;
import com.example.studycirclebackend.pojo.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
