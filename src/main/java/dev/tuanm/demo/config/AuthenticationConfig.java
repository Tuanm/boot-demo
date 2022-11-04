package dev.tuanm.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.tuanm.demo.model.dto.DemoUserDetails;
import dev.tuanm.demo.repository.UserRepository;

@Configuration
public class AuthenticationConfig {
    private final UserRepository userRepository;

    public AuthenticationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(DemoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
