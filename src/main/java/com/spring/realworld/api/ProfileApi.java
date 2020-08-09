package com.spring.realworld.api;

import com.spring.realworld.api.exception.ResourceNotFoundException;
import com.spring.realworld.application.ProfileQueryService;
import com.spring.realworld.application.data.ProfileData;
import com.spring.realworld.core.user.FollowRelation;
import com.spring.realworld.core.user.User;
import com.spring.realworld.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RequestMapping("/profiles/{username}")
@RestController
public class ProfileApi {

    private UserRepository userRepository;
    private ProfileQueryService profileQueryService;

    @Autowired
    public ProfileApi(UserRepository userRepository,
                      ProfileQueryService profileQueryService) {
        this.userRepository = userRepository;
        this.profileQueryService = profileQueryService;
    }

    @GetMapping
    public ResponseEntity getProfile(@AuthenticationPrincipal User currentUser,
                                     @PathVariable("username") String username) {
        return profileQueryService.findByUsername(username, currentUser)
                .map(this::profileResponse)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping("follow")
    public ResponseEntity followUser(@AuthenticationPrincipal User currentUser,
                                     @PathVariable("username") String username) {
        return userRepository.findByUsername(username).map(target -> {
            FollowRelation followRelation = new FollowRelation(currentUser.getId(), target.getId());
            userRepository.saveRelation(followRelation);
            return profileResponse(profileQueryService.findByUsername(username, currentUser).get());
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping("follow")
    public ResponseEntity unfollowUser(@AuthenticationPrincipal User currentUser,
                                       @PathVariable("username") String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User target = userOptional.get();
            return userRepository.findRelation(currentUser.getId(), target.getId()).map(relation -> {
                userRepository.removeRelation(relation);
                return profileResponse(profileQueryService.findByUsername(username, currentUser).get());
            }).orElseThrow(ResourceNotFoundException::new);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    private ResponseEntity profileResponse(ProfileData profile) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("profile", profile);
        }});
    }

}