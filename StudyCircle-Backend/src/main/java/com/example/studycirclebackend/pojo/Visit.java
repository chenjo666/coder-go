package com.example.studycirclebackend.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
@Data
@TableName("visit")
public class Visit {
    @TableId("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("post_id")
    private Long postId;
    @TableField("visit_time")
    private Date visitTime;
}
