package com.aravindweb.gatewayservice.exceptions;

import org.springframework.http.HttpStatus;

public class AuthException extends GatewayServerException {
    public AuthException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
