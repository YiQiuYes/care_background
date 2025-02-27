package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name="UserDTO",description ="用户信息" )
public class UserDTO {
    @Schema(name = "password",description = "密码")
    private String password;
    @Schema(name = "phone",description = "手机号")
    private String phone;
}
