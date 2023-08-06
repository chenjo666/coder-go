package com.cj.codergobackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ticket")
public class Ticket {
    @TableId
    private Long id;
    @TableField("token")
    private String token;
    @TableField("user_id")
    private Long userId;
    @TableField("is_valid")
    private Integer isValid;
    @TableField("expire")
    private Date expire;
}
