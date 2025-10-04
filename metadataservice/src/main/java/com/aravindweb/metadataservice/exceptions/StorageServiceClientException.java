package com.aravindweb.metadataservice.exceptions;

import org.springframework.http.HttpStatus;

public class StorageServiceClientException extends MetaDataServiceException {
    public StorageServiceClientException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
