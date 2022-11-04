package dev.tuanm.demo.model.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull

    private String firstName;
    @NotNull

    private String lastName;
}
