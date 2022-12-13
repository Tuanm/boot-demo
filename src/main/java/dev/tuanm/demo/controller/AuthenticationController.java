package dev.tuanm.demo.controller;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.tuanm.demo.common.constant.PathConstants;
import dev.tuanm.demo.common.exception.UnauthorizedRequestException;
import dev.tuanm.demo.model.request.AuthenticationRequest;
import dev.tuanm.demo.model.request.UserRegistrationRequest;
import dev.tuanm.demo.model.response.JwtResponse;
import dev.tuanm.demo.service.UserService;
import dev.tuanm.demo.utils.JwtUtils;

@RestController
public class AuthenticationController {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthenticationController(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping(PathConstants.API_AUTHENTICATION_URL)
    public JwtResponse authenticate(
            @RequestBody @NotNull AuthenticationRequest request) {
        return this.userService.exists(request)
                .map(userDetails -> {
                    String token = this.jwtUtils.generateToken(userDetails);
                    return JwtResponse.builder().token(token).build();
                })
                .orElseThrow(UnauthorizedRequestException::new);
    }

    @PostMapping(PathConstants.API_REGISTRATION_URL)
    public void register(
            @RequestBody @NotNull UserRegistrationRequest userRegistrationRequest) {
        this.userService.createUser(userRegistrationRequest);
    }
}
