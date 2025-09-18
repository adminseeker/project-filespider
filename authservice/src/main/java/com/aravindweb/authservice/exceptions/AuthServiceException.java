package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class AuthServiceException extends RuntimeException{
    public AuthServiceException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
