package com.example.springbootmybatis.mapper;

import com.example.springbootmybatis.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserXmlMapper {
    public List<User> findAll();
}
