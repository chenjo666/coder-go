package com.example.studycirclebackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studycirclebackend.pojo.Letter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LetterMapper extends BaseMapper<Letter> {
}
