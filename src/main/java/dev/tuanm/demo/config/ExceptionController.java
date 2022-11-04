package dev.tuanm.demo.config;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.tuanm.demo.common.exception.BaseWebException;
import dev.tuanm.demo.common.exception.UnauthorizedRequestException;
import dev.tuanm.demo.common.message.UserMessages;
import dev.tuanm.demo.model.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(UsernameNotFoundException.class)
    public void handleUsernameNotFoundRequest(UsernameNotFoundException ex, HttpServletRequest request) {
        throw new UnauthorizedRequestException(UserMessages.USERNAME_NOT_FOUND, ex);
    }

    @ExceptionHandler(BaseWebException.class)
    public ResponseEntity<ErrorResponse> handleErrorRequest(BaseWebException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(from(ex, request));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleRest(Throwable ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(from(ex, request));
    }

    private ErrorResponse from(Throwable ex, HttpServletRequest request) {
        HttpStatus status = (ex instanceof BaseWebException)
                ? ((BaseWebException) ex).getHttpStatus()
                : HttpStatus.INTERNAL_SERVER_ERROR;
        return ErrorResponse.builder()
                .timestamp(new Date().getTime())
                .status(status.value())
                .path(request.getServletPath())
                .message(ex.getMessage())
                .build();
    }
}
