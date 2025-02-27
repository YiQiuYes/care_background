package com.linping.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linping.care.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
