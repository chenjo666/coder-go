package com.example.studycirclebackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studycirclebackend.pojo.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
