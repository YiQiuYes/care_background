package com.linping.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "AddressDTO", description = "地址信息类")
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    @Schema(name = "id", description = "主键id")
    private Integer id;
    @Schema(name = "name", description = "联系人")
    private String name;
    @Schema(name = "phone", description = "联系电话")
    private String phone;
    @Schema(name = "province", description = "省份")
    private String province;
    @Schema(name = "city", description = "城市")
    private String city;
    @Schema(name = "district", description = "区县")
    private String district;
    @Schema(name = "detail", description = "详细地址")
    private String detail;
    @Schema(name = "isDefault", description = "是否默认地址", defaultValue = "false")
    private Boolean isDefault;
}
