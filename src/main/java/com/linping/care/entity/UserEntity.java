package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("phone")
    private String phone;
    @TableField("refresh_token")
    private String refreshToken;
    @TableField("auth")
    private Integer auth;
    @TableField("introduction")
    private String introduction;
    @TableField("nursing_role")
    private Integer nursingRole;
    @TableField("own_nursing_id")
    private Integer ownNursingId;
}
