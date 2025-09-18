package com.aravindweb.userservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class UserServiceException extends RuntimeException{
    public UserServiceException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
