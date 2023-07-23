package com.cj.studycirclebackend.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
@Data
@TableName("letter")
public class Letter {
    @TableId("id")
    private Long id;
    @TableField("user_from_id")
    private Long userFromId;
    @TableField("user_to_id")
    private Long userToId;
    @TableField("content")
    private String content;
    @TableField("send_time")
    private Date sendTime;
    @TableField("is_read")
    private Integer isRead;
}