package com.cj.studycirclebackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("blocked_letter")
public class BlockedLetter {
    @TableId
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("blocked_user_id")
    private Long blockedUserId;
}
