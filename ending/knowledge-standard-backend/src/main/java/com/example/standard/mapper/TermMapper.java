package com.example.standard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.standard.entity.Term;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TermMapper extends BaseMapper<Term> {
}