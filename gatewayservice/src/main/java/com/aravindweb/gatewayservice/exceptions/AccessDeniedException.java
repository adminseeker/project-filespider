package com.aravindweb.gatewayservice.exceptions;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends GatewayServerException {
    public AccessDeniedException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.FORBIDDEN;
    }
}
