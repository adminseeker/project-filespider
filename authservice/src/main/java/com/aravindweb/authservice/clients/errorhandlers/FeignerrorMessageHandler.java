package com.aravindweb.authservice.clients.errorhandlers;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aravindweb.authservice.clients.dto.ErrorResponse;
import com.aravindweb.authservice.exceptions.UserServiceClientException;
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
            return new UserServiceClientException(e.getMessage());
        }
        if(StringUtils.hasText(message.getError())){
            throw new UserServiceClientException(message.getError());
        }else{
            throw new UserServiceClientException(message.getErrorMessage());
        }

    }
}
