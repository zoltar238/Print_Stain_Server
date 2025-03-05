package com.github.zoltar238.PrintStainServer.exceptions;

import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import com.github.zoltar238.PrintStainServer.utils.TraceFormater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailConflict(EmailAlreadyExistsException ex) {
        log.error("User registration error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                    "This email is already registered. Please use a different one.",
                    null));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameConflict(UsernameAlreadyExistsException ex) {
        log.error("User registration error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                    "This username is already registered. Please use a different one.",
                    null));
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedErrors(UnexpectedException ex) {
        log.error("User registration error: {}. Trace: \n{}", ex.getMessage(), TraceFormater.format(ex));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.buildResponse(false, "Unexpected error", null));
    }
}
