package com.spring.realworld.application;

import com.spring.realworld.application.data.ProfileData;
import com.spring.realworld.application.data.UserData;
import com.spring.realworld.core.user.User;
import com.spring.realworld.infra.mybatis.readservice.UserReadService;
import com.spring.realworld.infra.mybatis.readservice.UserRelationshipQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileQueryService {

    private UserReadService userReadService;
    private UserRelationshipQueryService userRelationshipQueryService;

    @Autowired
    public ProfileQueryService(UserReadService userReadService,
                               UserRelationshipQueryService userRelationshipQueryService) {
        this.userReadService = userReadService;
        this.userRelationshipQueryService = userRelationshipQueryService;
    }

    public Optional<ProfileData> findByUsername(String username, User currentUser) {
        UserData userData = userReadService.findByUsername(username);
        if (userData == null) {
            return Optional.empty();
        } else {
            ProfileData profileData = new ProfileData(
                    userData.getId(),
                    userData.getUsername(),
                    userData.getBio(),
                    userData.getImage(),
                    userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId()));
            return Optional.of(profileData);
        }
    }

}