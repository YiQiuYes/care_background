package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Schema(name = "EmployeeDTO", description = "员工信息类")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "name", description = "员工姓名")
    private String name;
    @Schema(name = "phone", description = "员工手机号")
    private String phone;
    @Schema(name = "meta", description = "员工标签介绍")
    private String meta;
    @Schema(name = "description", description = "员工描述")
    private String description;
    @Schema(name = "userId", description = "员工服务对象")
    private Integer userId;
    @Schema(name = "status", description = "员工雇佣状态")
    private Integer status;
    @Schema(name = "jobNumber", description = "员工号")
    private BigInteger jobNumber;
    @Schema(name = "image", description = "头像")
    private String image;
}
