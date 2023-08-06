package com.cj.codergobackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("letter_blocked")
public class LetterBlocked {
    @TableId
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("blocked_user_id")
    private Long blockedUserId;
}
