package com.aravindweb.metadataservice.dto;

import lombok.Getter;

@Getter
public class FileUploadRequest {
    private String fileName;
    private String folderId;
    private String mime;
    private long fileSize;
    private long partSize;

}
