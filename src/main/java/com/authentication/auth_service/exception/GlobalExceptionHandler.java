package com.authentication.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleInvalidCredentialException(InvalidCredentialsException exception){
        return Map.of(
                "timeStamp", Instant.now(),
                "error", "INVALID_CREDENTIAL",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleAlreadyExistException(AlreadyExistException exception){
        return Map.of(
                "timeStamp", Instant.now(),
                "error", "ALREADY_EXIST",
                "message", exception.getMessage()
        );
    }
}
