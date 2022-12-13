package dev.tuanm.demo.security;

import javax.servlet.Filter;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dev.tuanm.demo.common.constant.AuthorityConstants;
import dev.tuanm.demo.common.constant.PathConstants;
import dev.tuanm.demo.security.filter.PrivateResourceFilter;
import dev.tuanm.demo.security.filter.JwtFilter;
import dev.tuanm.demo.utils.PrivateResourceUtils;
import dev.tuanm.demo.utils.JwtUtils;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    protected final UserDetailsService userService;
    protected final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            UserDetailsService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    protected void configureWithFilter(HttpSecurity http, Filter filter) throws Exception {
        if (filter instanceof JwtFilter) {
            http.antMatcher(PathConstants.JWT_AUTH_URL_PATTERN)
                    .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(PathConstants.AUTH_WHITELIST).permitAll()
                    .antMatchers(PathConstants.ADMIN_AUTH_LIST).hasAuthority(AuthorityConstants.ROLE_ADMIN)
                    .anyRequest().authenticated();
        } else if (filter instanceof PrivateResourceFilter) {
            http.antMatcher(PathConstants.PRIVATE_AUTH_URL_PATTERN)
                    .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(PathConstants.AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated();
        }
        http.csrf().disable().httpBasic();
    }

    @Order(1)
    @EnableWebSecurity
    public static class JwtConfig extends SecurityConfig {
        private final JwtUtils jwtUtils;

        public JwtConfig(
                UserDetailsService userService,
                PasswordEncoder passwordEncoder,
                JwtUtils jwtUtils) {
            super(userService, passwordEncoder);
            this.jwtUtils = jwtUtils;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.configureWithFilter(http, new JwtFilter(jwtUtils, userService));
        }
    }

    @Order(2)
    @EnableWebSecurity
    public static class PrivateResourceConfig extends SecurityConfig {
        private final PrivateResourceUtils privateResourceUtils;

        public PrivateResourceConfig(
                UserDetailsService userService,
                PasswordEncoder passwordEncoder,
                PrivateResourceUtils privateResourceUtils) {
            super(userService, passwordEncoder);
            this.privateResourceUtils = privateResourceUtils;

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.configureWithFilter(http, new PrivateResourceFilter(privateResourceUtils));
        }
    }
}
