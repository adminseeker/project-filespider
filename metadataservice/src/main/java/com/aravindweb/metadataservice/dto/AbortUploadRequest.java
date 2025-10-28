package com.aravindweb.metadataservice.dto;

import lombok.Getter;

@Getter
public class AbortUploadRequest {
    private String fileId;
    private String uploadId;
}
