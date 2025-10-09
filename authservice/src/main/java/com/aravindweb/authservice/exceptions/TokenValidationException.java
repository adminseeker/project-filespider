package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class TokenValidationException extends AuthServiceException{
    
    public TokenValidationException(String message){
        super(message);
    }
    
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}