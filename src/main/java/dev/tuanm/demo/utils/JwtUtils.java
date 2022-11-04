package dev.tuanm.demo.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains a number of utilities for JSON Web Token manipulation.
 */
@Component
public class JwtUtils implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "server.security.authentication.jwt")
    public static class JwtProperties implements Serializable {
        /**
         * The total seconds that a token keep being valid after its creation.
         */
        private Long validityInSeconds;
        /**
         * The secret key used in encoding-decoding algorithm.
         */
        private String secretKey;
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private <T> T safeGetClaimFromToken(String token, Function<Claims, T> claimsResolver, T defaultValue) {
        try {
            return getClaimFromToken(token, claimsResolver);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the expiration of a token.
     *
     * @param token the token.
     */
    private Date getExpirationFromToken(String token) {
        return safeGetClaimFromToken(token, Claims::getExpiration, null);
    }

    /**
     * Checks if a token has been expired or not.
     *
     * @param token the token.
     * @return {@code true} if the token is expired; otherwise {@code false}.
     */
    private boolean isTokenExpired(String token) {
        return Optional.ofNullable(getExpirationFromToken(token))
                .map(expiration -> expiration.before(new Date()))
                .orElse(true);
    }

    /**
     * Retrieves the username from a token.
     *
     * @param token the token.
     */
    public String getUsernameFromToken(String token) {
        return safeGetClaimFromToken(token, Claims::getSubject, null);
    }

    /**
     * Checks if a token is valid for a specific user or not.
     *
     * @param token       the token.
     * @param userDetails the {@link UserDetails} of the user.
     * @return {@code true} if valid; otherwise, {@code false}.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return Objects.equals(username, userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Generates a token for a specific user.
     *
     * @param userDetails the {@link UserDetails} of the user.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis() + jwtProperties.getValidityInSeconds() * MILLISECONDS_PER_SECOND))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();
    }
}
