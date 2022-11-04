package dev.tuanm.demo.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getLoggedUsername() {
        return Optional.ofNullable(getLoggedUserDetails())
                .map(UserDetails::getUsername)
                .orElse(null);
    }

    public Collection<String> getLoggedAuthorities() {
        return Optional.ofNullable(getLoggedUserDetails())
                .map(UserDetails::getAuthorities)
                .orElse(Arrays.asList())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private UserDetails getLoggedUserDetails() {
        Optional<Object> user = Optional.ofNullable(SecurityContextHolder.getContext()
                .getAuthentication()).map(Authentication::getPrincipal);
        if (user.isPresent() && (user.get() instanceof UserDetails)) {
            return (UserDetails) user.get();
        }
        return null;
    }
}
