package com.aravindweb.storageservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class StorageServiceException extends RuntimeException{
    public StorageServiceException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
