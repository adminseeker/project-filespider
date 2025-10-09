package com.aravindweb.gatewayservice.ExceptionHandlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aravindweb.gatewayservice.exceptions.GatewayServerException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayServerException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(GatewayServerException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("errorMessage", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}

