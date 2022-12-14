package dev.tuanm.demo.security.filter;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import dev.tuanm.demo.common.annotation.Permitted;

@Component
public class ServletRequestChecker {

    private final ApplicationContext applicationContext;

    public ServletRequestChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Checks if an {@link HttpServletRequest} is permitted or not.
     *
     * @param request the request.
     * @return {@code true} if the request is handled from a method annotated
     *         with {@link Permitted}; otherwise, {@code false}.
     * @throws Exception when something wrong occurs.
     * @see Permitted
     */
    public boolean isRequestPermitted(HttpServletRequest request) {
        for (Map.Entry<String, HandlerMapping> entry : this.applicationContext
                .getBeansOfType(HandlerMapping.class).entrySet()) {
            try {
                HandlerExecutionChain executionChain = entry.getValue().getHandler(request);
                Permitted permitted = Optional.ofNullable(executionChain)
                        .map(HandlerExecutionChain::getHandler)
                        .map(HandlerMethod.class::cast)
                        .map(HandlerMethod::getMethod)
                        .map(method -> method.getAnnotation(Permitted.class))
                        .orElse(null);

                if (permitted != null) {
                    return true;
                }
            } catch (Exception ex) {
                // don't care!
            }
        }

        return false;
    }

    /**
     * Retrieves the {@code Authentication} from an {@link HttpServletRequest}.
     *
     * @param request the request.
     */
    public Authentication check(HttpServletRequest request) {
        if (this.isRequestPermitted(request)) {
            return new GuestAuthenticationToken();
        }
        return null;
    }
}
