package com.spring.realworld.application;

import com.spring.realworld.application.data.UserData;
import com.spring.realworld.infra.mybatis.readservice.UserReadService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserQueryService {

    private UserReadService userReadService;

    public UserQueryService(UserReadService userReadService) {
        this.userReadService = userReadService;
    }

    public Optional<UserData> findById(String id) {
        return Optional.ofNullable(userReadService.findById(id));
    }

}