package com.spring.realworld.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.spring.realworld.api.exception.InvalidRequestException;
import com.spring.realworld.application.UserQueryService;
import com.spring.realworld.application.data.UserData;
import com.spring.realworld.application.data.UserWithToken;
import com.spring.realworld.core.user.User;
import com.spring.realworld.core.user.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/user")
@RestController
public class CurrentUserApi {

    private UserRepository userRepository;
    private UserQueryService userQueryService;

    @Autowired
    public CurrentUserApi(UserRepository userRepository,
                          UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
    }

    @GetMapping
    public ResponseEntity currentUser(@AuthenticationPrincipal User currentUser,
                                      @RequestHeader(value = "Authorization") String token) {
        UserData userData = userQueryService.findById(currentUser.getId()).get();
        return ResponseEntity.ok(userResponse(new UserWithToken(userData, token.split(" ")[1])));
    }

    @PutMapping
    public ResponseEntity updateProfile(@AuthenticationPrincipal User currentUser,
                                        @RequestHeader("Authorization") String token,
                                        @Valid @RequestBody UpdateUserParam updateUserParam,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        checkInput(currentUser, updateUserParam, bindingResult);
        currentUser.update(
                updateUserParam.getEmail(),
                updateUserParam.getUsername(),
                updateUserParam.getPassword(),
                updateUserParam.getBio(),
                updateUserParam.getImage());
        userRepository.save(currentUser);
        UserData userData = userQueryService.findById(currentUser.getId()).get();
        return ResponseEntity.ok(userResponse(new UserWithToken(userData, token.split(" ")[1])));
    }

    private void checkInput(User currentUser,
                            UpdateUserParam updateUserParam,
                            BindingResult bindingResult) {
        if (!"".equals(updateUserParam.getUsername())) {
            Optional<User> byUsername = userRepository.findByUsername(updateUserParam.getUsername());
            if (byUsername.isPresent() && !byUsername.get().equals(currentUser)) {
                bindingResult.rejectValue("username", "DUPLICATED", "username already exist");
            }
        }

        if (!"".equals(updateUserParam.getEmail())) {
            Optional<User> byEmail = userRepository.findByEmail(updateUserParam.getEmail());
            if (byEmail.isPresent() && !byEmail.get().equals(currentUser)) {
                bindingResult.rejectValue("email", "DUPLICATED", "email already exist");
            }
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private Map<String, Object> userResponse(UserWithToken userWithToken) {
        return new HashMap<String, Object>() {{
            put("user", userWithToken);
        }};
    }

}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class UpdateUserParam {
    @Email(message = "should be an email")
    private String email = "";
    private String password = "";
    private String username = "";
    private String bio = "";
    private String image = "";
}