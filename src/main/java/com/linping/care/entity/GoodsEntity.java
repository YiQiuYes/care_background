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
@TableName("goods")
@AllArgsConstructor
@NoArgsConstructor
public class GoodsEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "description")
    private String description;
    @TableField(value = "type")
    private String type;
    @TableField(value = "price")
    private BigDecimal price;
    @TableField(value = "is_active")
    private Integer isActive;
    @TableField(value = "create_time")
    private Timestamp createTime;
}
