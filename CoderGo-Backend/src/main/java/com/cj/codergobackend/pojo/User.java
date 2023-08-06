package com.cj.codergobackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId("id")
    private Long id;
    @TableField("email")
    private String email;
    @TableField("password")
    private String password;
    @TableField("salt")
    private String salt;
    @TableField("username")
    private String username;
    @TableField("type")
    private Integer type;
    @TableField("is_register")
    private Integer isRegister;
    @TableField("activation_code")
    private String activationCode;
    @TableField("avatar")
    private String avatar;
    @TableField("created_at")
    private Date createdAt;
}
