package dev.tuanm.demo.controller;

import java.util.Optional;

import javax.validation.constraints.NotNull;

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
    public UserInfoResponse info() {
        return Optional.ofNullable(this.securityUtils.getLoggedUsername())
                .map(this::getUserInfo)
                .orElseThrow(UnauthorizedRequestException::new);
    }

    @GetMapping(PathConstants.API_ADMIN_USERS_INFO_URL)
    public UserInfoResponse info(@PathVariable @NotNull String username) {
        return this.getUserInfo(username);
    }

    private UserInfoResponse getUserInfo(String username) {
        return this.userService.getUserInfo(username)
                .orElseThrow(UnauthorizedRequestException::new);
    }
}
