package com.spring.realworld.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.spring.realworld.api.exception.InvalidRequestException;
import com.spring.realworld.application.UserQueryService;
import com.spring.realworld.application.data.UserData;
import com.spring.realworld.application.data.UserWithToken;
import com.spring.realworld.core.service.JwtService;
import com.spring.realworld.core.user.User;
import com.spring.realworld.core.user.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UsersApi {

    private String image;
    private JwtService jwtService;
    private UserRepository userRepository;
    private UserQueryService userQueryService;

    @Autowired
    public UsersApi(@Value("${image.default.url}") String image,
                    JwtService jwtService,
                    UserRepository userRepository,
                    UserQueryService userQueryService) {
        this.image = image;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
    }

    @PostMapping("")
    public ResponseEntity createUser(@Valid @RequestBody RegisterParam registerParam,
                                     BindingResult bindingResult) {
        checkInput(registerParam, bindingResult);
        User user = new User(
                registerParam.getEmail(),
                registerParam.getUsername(),
                registerParam.getPassword(),
                "",
                image);
        userRepository.save(user);
        UserData userData = userQueryService.findById(user.getId()).get();
        return ResponseEntity.status(201).body(userResponse(new UserWithToken(userData, jwtService.getToken(user))));
    }

    @PostMapping("/login")
    public ResponseEntity userLogin(@Valid @RequestBody LoginParam loginParam,
                                    BindingResult bindingResult) {
        Optional<User> optional = userRepository.findByEmail(loginParam.getEmail());
        if (optional.isPresent() && checkPassword(loginParam.getPassword(), optional.get().getPassword())) {
            UserData userData = userQueryService.findById(optional.get().getId()).get();
            return ResponseEntity.ok(userResponse(new UserWithToken(userData, jwtService.getToken(optional.get()))));
        } else {
            bindingResult.rejectValue("password", "INVALID", "invalid email or password");
            throw new InvalidRequestException(bindingResult);
        }
    }

    private void checkInput(@Valid @RequestBody RegisterParam registerParam,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

        if (userRepository.findByUsername(registerParam.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }

        if (userRepository.findByEmail(registerParam.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private boolean checkPassword(String checkPassword,
                                  String realPassword) {
        return checkPassword.equals(realPassword);
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
class LoginParam {
    @NotBlank(message = "can't be empty")
    @Email(message = "should be an email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String password;
}

@Getter
@JsonRootName("user")
@NoArgsConstructor
class RegisterParam {
    @NotBlank(message = "can't be empty")
    @Email(message = "should be an email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String username;
    @NotBlank(message = "can't be empty")
    private String password;
}