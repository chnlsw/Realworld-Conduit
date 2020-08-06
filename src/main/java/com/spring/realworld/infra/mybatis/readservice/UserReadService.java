package com.spring.realworld.infra.mybatis.readservice;

import com.spring.realworld.application.data.UserData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserReadService {

    UserData findById(@Param("id") String id);

    UserData findByUsername(@Param("username") String username);

}