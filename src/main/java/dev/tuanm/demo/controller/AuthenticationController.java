package dev.tuanm.demo.controller;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.tuanm.demo.common.constant.PathConstants;
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
    public ResponseEntity<JwtResponse> authenticate(
            @RequestBody @NotNull AuthenticationRequest request) {
        Optional<UserDetails> userDetails = this.userService.exists(request);
        if (userDetails.isPresent()) {
            String token = this.jwtUtils.generateToken(userDetails.get());
            return ResponseEntity.ok(JwtResponse.builder().token(token).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(PathConstants.API_REGISTRATION_URL)
    public void register(
            @RequestBody @NotNull UserRegistrationRequest userRegistrationRequest) {
        this.userService.createUser(userRegistrationRequest);
    }
}
