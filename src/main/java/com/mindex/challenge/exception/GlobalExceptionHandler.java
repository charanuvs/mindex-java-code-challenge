package com.mindex.challenge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class contains handler methods for uncaught exceptions generated across the application.
 * TODO: Add any app level exception handling.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(
            GlobalExceptionHandler.class);

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(
            Exception ex, WebRequest request) {
        LOG.error("Exception {} {}", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(
                "Something went wrong.", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<Object> handleEmployeeNotFoundException(Exception ex, WebRequest request) {
        LOG.warn("Request failed because employee with id does not exist in database. {} {}", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>("Employee does not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}