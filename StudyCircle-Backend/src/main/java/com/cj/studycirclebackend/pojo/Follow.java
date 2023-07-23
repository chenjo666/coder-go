package com.cj.studycirclebackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("follow")
public class Follow {
    @TableId("id")
    private Long id;
    @TableField("user_from_id")
    private Long userFromId;
    @TableField("user_to_id")
    private Long userToId;
}
