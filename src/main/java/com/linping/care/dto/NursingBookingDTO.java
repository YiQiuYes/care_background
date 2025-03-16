package com.linping.care.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name="NursingBookingDTO",description ="资讯信息" )
@AllArgsConstructor
@NoArgsConstructor
public class NursingBookingDTO {
    @Schema(name = "nursingId",description = "养老院id")
    private Integer nursingId;
    @Schema(name = "name",description = "联系人姓名")
    private String name;
    @TableField("联系人地址")
    private String address;
    @TableField("联系人电话")
    private String phone;
    @TableField("预约时间")
    private Long time;
    @TableField("预约内容")
    private String content;
}
