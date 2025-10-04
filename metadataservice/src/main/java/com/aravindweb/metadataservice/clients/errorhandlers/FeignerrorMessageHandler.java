package com.aravindweb.metadataservice.clients.errorhandlers;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.dto.ErrorResponse;
import com.aravindweb.metadataservice.exceptions.StorageServiceClientException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignerrorMessageHandler implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ErrorResponse.class);
        } catch (IOException e) {
            return new StorageServiceClientException(e.getMessage());
        }
        if(StringUtils.hasText(message.getError())){
            throw new StorageServiceClientException(message.getError());
        }else{
            throw new StorageServiceClientException(message.getErrorMessage());
        }

    }
}
