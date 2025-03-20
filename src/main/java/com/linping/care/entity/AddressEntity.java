package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("address")
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "name")
    private String name;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "province")
    private String province;
    @TableField(value = "city")
    private String city;
    @TableField(value = "district")
    private String district;
    @TableField(value = "detail")
    private String detail;
    @TableField(value = "is_default")
    private Boolean isDefault;
}
