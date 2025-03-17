package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Schema(name="NursingBookingDTO",description ="资讯信息" )
@AllArgsConstructor
@NoArgsConstructor
public class NursingBookingDTO {
    @Schema(name = "id",description = "主键id")
    private Integer id;
    @Schema(name = "nursingId",description = "养老院id")
    private Integer nursingId;
    @Schema(name = "name",description = "联系人姓名")
    private String name;
    @Schema(name = "address",description = "联系人地址")
    private String address;
    @Schema(name = "phone",description = "联系人电话")
    private String phone;
    @Schema(name = "time",description = "预约时间")
    private Timestamp time;
    @Schema(name = "content",description = "预约内容")
    private String content;
    @Schema(name = "status",description = "预约状态")
    private String status;
    @Schema(name = "nursingName",description = "养老院名称")
    private String nursingName;
    @Schema(name = "userName",description = "用户名名称")
    private String userName;
}
