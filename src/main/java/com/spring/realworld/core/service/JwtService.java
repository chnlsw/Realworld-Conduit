package com.spring.realworld.core.service;

import com.spring.realworld.core.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JwtService {

    String getToken(User user);

    Optional<String> getSubjectFromToken(String token);

}