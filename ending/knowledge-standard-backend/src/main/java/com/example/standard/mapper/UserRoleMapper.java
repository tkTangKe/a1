package com.example.standard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper {

    @Select("SELECT r.name FROM user_role ur JOIN role r ON ur.role_id = r.id WHERE ur.user_id = #{userId}")
    String getRoleNameByUserId(Integer userId);
}