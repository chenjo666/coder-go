package com.cj.codergobackend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.codergobackend.pojo.Letter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LetterMapper extends BaseMapper<Letter> {

    @Select("SELECT user_to_id FROM (" +
            "    SELECT user_to_id, MAX(created_at) AS newDate" +
            "    FROM letter" +
            "    WHERE user_from_id = #{userId} AND is_deleted_from_user = 0" +
            "    GROUP BY user_to_id" +
            "    UNION" +
            "    SELECT user_from_id, MAX(created_at) AS newDate" +
            "    FROM letter" +
            "    WHERE user_to_id = #{userId} AND is_deleted_to_user = 0" +
            "    GROUP BY user_from_id" +
            "    ORDER BY newDate DESC" +
            ") AS sub" +
            " GROUP BY user_to_id")
    List<Long> getToUserIds(@Param("userId") Long userId);
}
