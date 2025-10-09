package com.aravindweb.metadataservice.clients.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageServiceResponse {
    private String url;
    private String etag;
}
