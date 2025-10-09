package com.aravindweb.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class UserServiceClientException extends AuthServiceException {
    public UserServiceClientException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
