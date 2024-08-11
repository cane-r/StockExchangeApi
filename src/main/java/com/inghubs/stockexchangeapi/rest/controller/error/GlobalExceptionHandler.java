package com.inghubs.stockexchangeapi.rest.controller.error;

import com.inghubs.stockexchangeapi.models.exception.StockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ResponseEntity<Map<String,String >> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Map<String, String> exception = new HashMap<>();
        exception.put("message",ex.getMessage());
        return new ResponseEntity<>(exception,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NoHandlerFoundException.class,IllegalStateException.class, NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Map<String,String >> handleIllegalStateException(Exception ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Map<String, String> exception = new HashMap<>();
        exception.put("message",ex.getMessage());
        return new ResponseEntity<>(exception,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodArgumentExceptionErrorHandler(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(e-> e.getField(),e-> e.getDefaultMessage()));
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        body.put("message",errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    /**
     * Thrown when a stock with the same name already exists in the exchange
     * */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String >> handleStockException(DataIntegrityViolationException ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Map<String, String> exception = new HashMap<>();
        exception.put("message","Stock name is unique.So you can't add the same stock twice.");
        return new ResponseEntity<>(exception,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(StockException.class)
    public ResponseEntity<Map<String,String >> handleStockException(StockException ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Map<String, String> exception = new HashMap<>();
        exception.put("message",ex.getMessage());
        return new ResponseEntity<>(exception,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("Exception : {}" ,ex.getMessage());
        Set<ConstraintViolation<?>> set = ex.getConstraintViolations();
        Map<String, String> errorMap = set.stream().collect(Collectors.toMap(e-> e.getPropertyPath().toString(),e-> e.getMessage()));
        return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex,HttpServletRequest request) {
        log.error("Exception : " + ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}