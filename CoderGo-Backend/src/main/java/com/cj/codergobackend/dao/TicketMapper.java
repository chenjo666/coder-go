package com.cj.codergobackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.codergobackend.pojo.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
