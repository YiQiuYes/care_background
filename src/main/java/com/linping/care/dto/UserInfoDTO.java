package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name="UserInfoDTO",description ="用户信息" )
public class UserInfoDTO {
    @Schema(name = "id",description = "用户ID")
    private Integer id;
    @Schema(name = "username",description = "用户名")
    private String username;
    @Schema(name = "password",description = "密码")
    private String password;
    @Schema(name = "phone",description = "手机号")
    private String phone;
    @Schema(name = "refreshToken",description = "刷新token")
    private String refreshToken;
    @Schema(name = "auth",description = "权限")
    private Integer auth;
    @Schema(name = "avatar",description = "头像")
    private String avatar;
    @Schema(name = "introduction",description = "个人介绍")
    private String introduction;
}
