package com.example.studycirclebackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("conversation")
@Data
public class Conversation {
    @TableId
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("name")
    private String name;
    @TableField("create_time")
    private Date createTime;
    @TableField("is_deleted")
    private Integer isDeleted;
}
