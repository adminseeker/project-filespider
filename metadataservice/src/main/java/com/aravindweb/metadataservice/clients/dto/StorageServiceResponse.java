package com.aravindweb.metadataservice.clients.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageServiceResponse {
    private String url;
    private String etag;
    private String status;
    private String uploadId;
    private Map<Integer, String> urls;
}
