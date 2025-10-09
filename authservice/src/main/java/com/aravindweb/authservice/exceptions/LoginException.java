package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class LoginException extends AuthServiceException{
    
    public LoginException(String message){
        super(message);
    }
    
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
