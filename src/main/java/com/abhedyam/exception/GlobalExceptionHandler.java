package com.abhedyam.exception;

import com.abhedyam.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        String correlationId = MDC.get("correlationId");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        ErrorResponse error = new ErrorResponse(
            "Resource Not Found",
            ex.getMessage(),
            "RESOURCE_NOT_FOUND",
            correlationId,
            path
        );
        
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        String correlationId = MDC.get("correlationId");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        ErrorResponse error = new ErrorResponse(
            "Business Error",
            ex.getMessage(),
            ex.getErrorCode(),
            correlationId,
            path
        );
        
        log.warn("Business error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String correlationId = MDC.get("correlationId");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String message = errors.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "Validation Error",
            message,
            "VALIDATION_ERROR",
            correlationId,
            path
        );
        
        log.warn("Validation error: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String correlationId = MDC.get("correlationId");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        String message = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "Validation Error",
            message,
            "VALIDATION_ERROR",
            correlationId,
            path
        );
        
        log.warn("Constraint violation: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String correlationId = MDC.get("correlationId");
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        ErrorResponse error = new ErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred",
            "INTERNAL_ERROR",
            correlationId,
            path
        );
        
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
