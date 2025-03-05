package com.github.zoltar238.PrintStainServer.exceptions;

import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailConflict(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameConflict(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.buildResponse(false,
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedErrors(UnexpectedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.buildResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFound(ItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseBuilder.buildResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(CostOrPriceInvalidException.class)
    public ResponseEntity<Object> handleCostOrPriceInvalid(CostOrPriceInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseBuilder.buildResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<Object> handleImageProcessingErrors(ImageProcessingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.buildResponse(true, ex.getMessage(), null));
    }
}
