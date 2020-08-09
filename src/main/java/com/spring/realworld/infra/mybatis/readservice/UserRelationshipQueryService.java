package com.spring.realworld.infra.mybatis.readservice;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRelationshipQueryService {

    boolean isUserFollowing(@Param("userId") String userId, @Param("anotherUserId") String anotherUserId);

}