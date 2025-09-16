package com.aravindweb.userservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class UserServiceException extends Exception{
    public UserServiceException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
