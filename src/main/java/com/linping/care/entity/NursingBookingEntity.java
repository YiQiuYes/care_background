package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@TableName("nursing_booking")
@AllArgsConstructor
@NoArgsConstructor
public class NursingBookingEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("name")
    private String name;
    @TableField("address")
    private String address;
    @TableField("phone")
    private String phone;
    @TableField("time")
    private Timestamp time;
    @TableField("content")
    private String content;
    @TableField("user_id")
    private Integer userId;
    @TableField("nursing_id")
    private Integer nursingId;
    @TableField("status")
    private Integer status;
}
