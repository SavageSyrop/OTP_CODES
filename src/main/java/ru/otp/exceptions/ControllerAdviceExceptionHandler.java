package ru.otp.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionBody handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn(e.toString());
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleResourceNotFound(EntityNotFoundException e) {
        log.warn(e.toString());
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(IllegalStateException e) {
        log.warn(e.toString());
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied(RuntimeException e) {
        log.warn(e.toString());
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn(e.toString());
        ExceptionBody exceptionBody = new ExceptionBody(e.getMessage());
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existingMessage, newMessage) -> existingMessage + " " + newMessage)));
        return exceptionBody;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(ConstraintViolationException e) {
        log.warn(e.toString());
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        exceptionBody.setErrors(e.getConstraintViolations().stream().collect(Collectors.toMap(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage)));
        return exceptionBody;
    }

    @ExceptionHandler(value = {AuthenticationException.class, AuthorizationServiceException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAuthentication(RuntimeException e) {
        log.warn(e.toString());
        return new ExceptionBody(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(Exception e) {
        log.warn(e.toString());
        return new ExceptionBody("Internal error: " + e.getMessage());
    }
}
