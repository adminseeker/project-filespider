package com.aravindweb.metadataservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class MetaDataServiceException extends RuntimeException{
    public MetaDataServiceException(String message){
        super(message);
    }

    public abstract HttpStatus getStatusCode();
}
