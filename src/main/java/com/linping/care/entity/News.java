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
@TableName("news")
@AllArgsConstructor
@NoArgsConstructor
public class News {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("image_src")
    private String imageSrc;
    @TableField("source")
    private String source;
    @TableField("type")
    private String type;
    @TableField("create_time")
    private Timestamp createTime;
}
