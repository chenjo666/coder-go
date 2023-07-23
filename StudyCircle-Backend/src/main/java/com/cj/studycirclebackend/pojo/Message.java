package com.cj.studycirclebackend.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("message")
@Data
public class Message {
    @TableId
    private Long id;
    @TableField("conversation_id")
    private Long conversationId;
    @TableField("message_target_id")
    private Long messageTargetId;
    @TableField("role")
    private String role;
    @TableField("content")
    private String content;
    @TableField("tokens")
    private Long tokens;
    @TableField("send_time")
    private Date sendTime;
    @TableField("is_deleted")
    private Integer isDeleted;
}
