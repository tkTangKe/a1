package com.example.standard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.standard.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}