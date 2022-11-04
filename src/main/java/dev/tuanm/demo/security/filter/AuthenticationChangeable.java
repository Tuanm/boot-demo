package dev.tuanm.demo.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface AuthenticationChangeable {
    /**
     * Changes the current context's authentication.
     */
    default void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
