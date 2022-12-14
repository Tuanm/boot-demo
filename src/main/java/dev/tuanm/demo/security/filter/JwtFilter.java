package dev.tuanm.demo.security.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import dev.tuanm.demo.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtFilter implements Filter, AuthenticationChangeable {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ServletRequestChecker requestChecker;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public JwtFilter(
            ServletRequestChecker requestChecker,
            JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.requestChecker = requestChecker;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Optional<String> authorization = Optional.ofNullable(
                request.getHeader(AUTHORIZATION_HEADER));

        if (authorization.isPresent()) {
            String token = authorization.get();
            if (token.startsWith(BEARER_PREFIX)) {
                return token.substring(BEARER_PREFIX.length());
            }
        }

        return null;
    }

    private void validate(String username, String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (jwtUtils.isTokenValid(token, userDetails)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            setAuthentication(authentication);
        } else {
            setAuthentication(null);
        }
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(this.requestChecker.check(request))
                .ifPresentOrElse(this::setAuthentication, () -> {
                    Optional<String> token = Optional.ofNullable(getTokenFromRequest(request));
                    if (token.isPresent()) {
                        try {
                            String username = this.jwtUtils.getUsernameFromToken(token.get());
                            this.validate(username, token.get());
                        } catch (ExpiredJwtException ex) {
                            setAuthentication(null);
                        }
                    } else {
                        setAuthentication(null);
                    }
                });
        filterChain.doFilter(request, response);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        this.doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
}
