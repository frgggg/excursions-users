package com.excursions.users.controller;

import com.excursions.users.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static com.excursions.users.log.message.ExceptionControllerLogMessages.CONTROLLER_LOG_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    protected static ResponseEntity<String> exceptionControllerStringResponseEntity(
            Exception reasonException,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        log.error(
                CONTROLLER_LOG_EXCEPTION,
                reasonException.getClass().getSimpleName(),
                message,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString()
        );

        return new ResponseEntity<>(message, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, WebRequest request) {

        return exceptionControllerStringResponseEntity(
                ex,
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                BAD_REQUEST,
                request
        );

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<String> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return exceptionControllerStringResponseEntity(
                ex,
                ex.getMessage(),
                NOT_IMPLEMENTED,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<String> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex, WebRequest request) {
        return exceptionControllerStringResponseEntity(
                ex,
                ex.getMessage(),
                BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<String> serviceExceptionHandler(ServiceException ex, WebRequest request) {
        return exceptionControllerStringResponseEntity(
                ex,
                ex.getMessage(),
                BAD_REQUEST,
                request
        );
    }
}
