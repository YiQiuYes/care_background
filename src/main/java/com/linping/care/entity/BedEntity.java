package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@TableName("bed")
@AllArgsConstructor
@NoArgsConstructor
public class BedEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "nursing_id")
    private Integer nursingId;
    @TableField(value = "size")
    private Integer size;
    @TableField(value = "address")
    private String address;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "meta")
    private String meta;
    @TableField(value = "price")
    private BigDecimal price;
    @TableField(value = "description")
    private String description;
    @TableField(value = "aptitude")
    private Integer aptitude;
    @TableField(value = "own_id")
    private Integer ownId;
}
