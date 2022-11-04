package dev.tuanm.demo.service.impl;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.tuanm.demo.model.dto.DemoUserDetails;
import dev.tuanm.demo.model.entity.User;
import dev.tuanm.demo.model.request.AuthenticationRequest;
import dev.tuanm.demo.model.request.UserRegistrationRequest;
import dev.tuanm.demo.model.response.UserInfoResponse;
import dev.tuanm.demo.repository.UserRepository;
import dev.tuanm.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDetails> exists(AuthenticationRequest request) {
        String username = request.getUsername();
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isPresent() && this.passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return Optional.of(new DemoUserDetails(user.get()));
        }
        return Optional.ofNullable(null);
    }

    @Override
    public Optional<UserInfoResponse> getUserInfo(String username) {
        return this.userRepository.findByUsername(username)
                .map(user -> UserInfoResponse.builder()
                        .username(username)
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build());
    }

    @Override
    public void createUser(UserRegistrationRequest userRegistration) {
        this.userRepository.findByUsername(userRegistration.getUsername())
                .ifPresentOrElse(null, () -> {
                    User user = User.builder()
                            .username(userRegistration.getUsername())
                            .password(this.passwordEncoder.encode(userRegistration.getPassword()))
                            .email(userRegistration.getEmail())
                            .firstName(userRegistration.getFirstName())
                            .lastName(userRegistration.getLastName())
                            .roles(new HashSet<>())
                            .build();
                    this.userRepository.save(user);
                });
    }
}
