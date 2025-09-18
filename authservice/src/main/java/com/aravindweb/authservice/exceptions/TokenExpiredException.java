package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthServiceException{
    
    public TokenExpiredException(String message){
        super(message);
    }
    
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}