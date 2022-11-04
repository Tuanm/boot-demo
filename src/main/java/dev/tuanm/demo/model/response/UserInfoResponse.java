package dev.tuanm.demo.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
