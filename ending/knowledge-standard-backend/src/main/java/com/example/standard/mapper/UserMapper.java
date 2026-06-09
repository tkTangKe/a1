package com.example.standard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.standard.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 即拥有基础的 CRUD 方法，无需额外代码
}