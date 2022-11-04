package dev.tuanm.demo.utils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
public class PrivateResourceUtils {

    private final PrivateProperties properties;

    public PrivateResourceUtils(PrivateProperties properties) {
        this.properties = properties;
    }

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "server.security.authentication.private")
    public static class PrivateProperties implements Serializable {
        private String headerKey;
        private String secretKey;
    }

    public boolean isRequestValid(HttpServletRequest request) {
        Optional<String> key = Optional.ofNullable(
            request.getHeader(properties.getHeaderKey())
        );
        if (key.isPresent()) {
            return Objects.equals(properties.getSecretKey(), key.get());
        }
        return false;
    }
}
