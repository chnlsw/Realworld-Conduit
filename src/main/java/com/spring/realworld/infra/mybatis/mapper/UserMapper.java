package com.spring.realworld.infra.mybatis.mapper;

import com.spring.realworld.core.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    void insert(@Param("user") User user);

    void update(@Param("user") User user);

    User findById(@Param("id") String id);

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

}