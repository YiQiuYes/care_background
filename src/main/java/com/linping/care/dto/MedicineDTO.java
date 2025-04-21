package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "MedicineDTO", description = "药品信息类")
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "name", description = "药物名称")
    private String name;
    @Schema(name = "dosage", description = "药剂量")
    private String dosage;
    @Schema(name = "startTime", description = "开始时间")
    private String startTime;
    @Schema(name = "endTime", description = "结束时间")
    private String endTime;
    @Schema(name = "employeeId", description = "雇员id")
    private Integer employeeId;
    @Schema(name = "employeeImage", description = "雇员头像")
    private String employeeImage;
}
