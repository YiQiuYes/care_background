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
@TableName("comment")
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "goods_id")
    private Integer goodsId;
    @TableField(value = "content")
    private String content;
    @TableField(value = "grade")
    private Integer grade;
    @TableField(value = "create_time")
    private Timestamp createTime;
}
