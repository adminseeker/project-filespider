package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthServiceException{
    
    public UserNotFoundException(String message){
        super(message);
    }
    
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}