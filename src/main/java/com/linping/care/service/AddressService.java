package com.linping.care.service;

import com.github.yulichang.base.MPJBaseService;
import com.linping.care.dto.AddressDTO;
import com.linping.care.entity.AddressEntity;

import java.util.List;

public interface AddressService extends MPJBaseService<AddressEntity> {
    List<AddressDTO> addressList(Integer userId);

    AddressEntity defaultAddress(Integer userId);
}
