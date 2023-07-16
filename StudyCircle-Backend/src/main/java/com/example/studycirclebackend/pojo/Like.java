package com.example.studycirclebackend.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
@Data
@TableName("likes")
public class Like {
    @TableId("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("object_type")
    private String objectType;
    @TableField("object_id")
    private Long objectId;
}