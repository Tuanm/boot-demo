package dev.tuanm.demo.controller;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dev.tuanm.demo.common.constant.PathConstants;
import dev.tuanm.demo.common.exception.UnauthorizedRequestException;
import dev.tuanm.demo.model.response.UserInfoResponse;
import dev.tuanm.demo.service.UserService;
import dev.tuanm.demo.utils.SecurityUtils;

@RestController
public class UserController {

    private final SecurityUtils securityUtils;
    private final UserService userService;

    public UserController(
            UserService userService,
            SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping(PathConstants.API_USERS_INFO_URL)
    public ResponseEntity<UserInfoResponse> info() {
        Optional<String> username = Optional.ofNullable(this.securityUtils.getLoggedUsername());
        if (username.isPresent()) {
            return this.getUserInfo(username.get());
        }
        throw new UnauthorizedRequestException();
    }

    @GetMapping(PathConstants.API_ADMIN_USERS_INFO_URL)
    public ResponseEntity<UserInfoResponse> info(@PathVariable @NotNull String username) {
        return this.getUserInfo(username);
    }

    private ResponseEntity<UserInfoResponse> getUserInfo(String username) {
        Optional<UserInfoResponse> userInfo = this.userService.getUserInfo(username);
        if (userInfo.isPresent()) {
            return ResponseEntity.ok(userInfo.get());
        }
        throw new UnauthorizedRequestException();
    }
}
