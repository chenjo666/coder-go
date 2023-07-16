package com.example.studycirclebackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studycirclebackend.pojo.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
