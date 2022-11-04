package dev.tuanm.demo.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseWebException extends RuntimeException {
    protected final HttpStatus httpStatus;

    public BaseWebException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public BaseWebException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null);
    }

    public BaseWebException(HttpStatus httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }
}
