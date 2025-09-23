package com.aravindweb.gatewayservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class GatewayServerException extends RuntimeException{
    public GatewayServerException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
