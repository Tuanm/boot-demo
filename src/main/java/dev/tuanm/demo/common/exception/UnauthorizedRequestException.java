package dev.tuanm.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * An exception thrown when a request is not authorized.
 */
public class UnauthorizedRequestException extends BaseWebException {
    public UnauthorizedRequestException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedRequestException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedRequestException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }
}
