package com.linping.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("nursing")
@AllArgsConstructor
@NoArgsConstructor
public class NursingEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("name")
    private String name;
    @TableField("address")
    private String address;
    @TableField("location")
    private String location;
    @TableField("phone")
    private String phone;
    @TableField("content")
    private String content;
    @TableField("time")
    private String time;
    @TableField("bunk_count")
    private Integer bunkCount;
    @TableField("worker_count")
    private Integer workerCount;
    @TableField("size")
    private Long size;
    @TableField("aptitude")
    private Integer aptitude;
}
