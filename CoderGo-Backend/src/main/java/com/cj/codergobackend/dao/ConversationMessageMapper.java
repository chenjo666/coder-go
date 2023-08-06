package com.cj.codergobackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.codergobackend.pojo.ConversationMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationMessageMapper extends BaseMapper<ConversationMessage> {
}
