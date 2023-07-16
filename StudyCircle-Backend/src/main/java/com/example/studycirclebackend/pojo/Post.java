package com.example.studycirclebackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("post")
public class Post {
    @TableId("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("publish_time")
    private Date publishTime;
    @TableField("type")
    private String type;
    @TableField("is_top")
    private Integer isTop;
    @TableField("is_gem")
    private Integer isGem;
    @TableField("score")
    private Double score;
    @TableField("tags")
    private String tags;
}
