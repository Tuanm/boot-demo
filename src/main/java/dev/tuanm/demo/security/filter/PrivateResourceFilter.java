package dev.tuanm.demo.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import dev.tuanm.demo.utils.PrivateResourceUtils;

public class PrivateResourceFilter implements Filter, AuthenticationChangeable {

    private final ServletRequestChecker requestChecker;
    private final PrivateResourceUtils privateResourceUtils;

    public PrivateResourceFilter(
            ServletRequestChecker requestChecker,
            PrivateResourceUtils privateResourceUtils) {
        this.requestChecker = requestChecker;
        this.privateResourceUtils = privateResourceUtils;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(this.requestChecker.check(request))
                .ifPresentOrElse(this::setAuthentication, () -> {
                    if (privateResourceUtils.isRequestValid(request)) {
                        Authentication authentication = new PrivateResourceAuthToken(null, Arrays.asList());
                        setAuthentication(authentication);
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
