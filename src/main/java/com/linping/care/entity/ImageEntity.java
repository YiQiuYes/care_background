package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("image")
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "src")
    private String src;
    @TableField(value = "news_id")
    private Integer newsId;
    @TableField(value = "user_id")
    private Integer userId;
}
