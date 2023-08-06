package com.cj.codergobackend.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("conversation_message")
@Data
public class ConversationMessage {
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
    @TableField("created_at")
    private Date createdAt;
    @TableField("is_deleted")
    private Integer isDeleted;
}
