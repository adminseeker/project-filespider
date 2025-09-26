package com.aravindweb.gatewayservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class AccessDeniedException extends GatewayServerException {
    public AccessDeniedException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.FORBIDDEN;
    }
}
