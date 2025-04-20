package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@TableName("employee")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "meta")
    private String meta;
    @TableField(value = "description")
    private String description;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "job_number")
    private BigInteger jobNumber;
    @TableField(value = "nursing_id")
    private Integer nursingId;
}
