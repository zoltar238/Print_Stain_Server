package com.github.zoltar238.PrintStainServer.exceptions;

import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailConflict(EmailAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                    "This email is already registered. Please use a different one.",
                    null));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameConflict(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                    "This username is already registered. Please use a different one.",
                    null));
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedErrors(UnexpectedException ex) {
        log.error(ex.getMessage(), ex.getCause(), ex.getStackTrace(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.buildResponse(false, "Unexpected error", null));
    }
}
