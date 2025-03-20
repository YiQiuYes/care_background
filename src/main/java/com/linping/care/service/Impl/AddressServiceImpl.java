package com.linping.care.service.Impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.linping.care.dto.AddressDTO;
import com.linping.care.entity.AddressEntity;
import com.linping.care.mapper.AddressMapper;
import com.linping.care.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl extends MPJBaseServiceImpl<AddressMapper, AddressEntity> implements AddressService {
    private final AddressMapper addressMapper;

    @Override
    public List<AddressDTO> addressList(Integer userId) {
        MPJLambdaQueryWrapper<AddressEntity> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(AddressEntity.class);
        wrapper.eq(AddressEntity::getUserId, userId);
        return addressMapper.selectJoinList(AddressDTO.class, wrapper);
    }

    @Override
    public AddressEntity defaultAddress(Integer userId) {
        MPJLambdaQueryWrapper<AddressEntity> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(AddressEntity.class);
        wrapper.eq(AddressEntity::getUserId, userId);
        wrapper.eq(AddressEntity::getIsDefault, true);
        return addressMapper.selectOne(wrapper);
    }
}
