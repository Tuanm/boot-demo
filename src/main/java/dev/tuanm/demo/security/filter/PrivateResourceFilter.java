package dev.tuanm.demo.security.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.tuanm.demo.utils.PrivateResourceUtils;

public class PrivateResourceFilter extends OncePerRequestFilter implements AuthenticationChangeable {

    private final PrivateResourceUtils privateResourceUtils;

    public PrivateResourceFilter(PrivateResourceUtils privateResourceUtils) {
        this.privateResourceUtils = privateResourceUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (privateResourceUtils.isRequestValid(request)) {
            Authentication authentication = new PrivateResourceAuthToken(null, Arrays.asList());
            setAuthentication(authentication);
        } else {
            setAuthentication(null);
        }
        filterChain.doFilter(request, response);
    }
}
