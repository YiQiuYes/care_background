package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("orders")
@AllArgsConstructor
@NoArgsConstructor
public class OrdersEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "goods_id")
    private Integer goodsId;
    @TableField(value = "create_time")
    private Timestamp createTime;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "address")
    private String address;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "order_id")
    private String orderId;
    @TableField(value = "price")
    private BigDecimal price;
    @TableField(value = "count")
    private Integer count;
}
