package com.example.studycirclebackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notice")
public class Notice {
    @TableId("id")
    private Long id;
    @TableField("user_from_id")
    private Long userFromId;
    @TableField("user_to_id")
    private Long userToId;
    @TableField("notice_type")
    private Integer noticeType;
    @TableField("post_id")
    private Long postId;
    @TableField("is_read")
    private Integer isRead;
    @TableField("notice_time")
    private Date noticeTime;
}
