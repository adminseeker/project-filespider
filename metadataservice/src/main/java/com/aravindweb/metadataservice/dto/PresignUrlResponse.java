package com.aravindweb.metadataservice.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresignUrlResponse {
    private boolean multiPartUpload;
    private Map<Integer,String> partsUrls;
    private String uploadId;
    private String fileId;
}
