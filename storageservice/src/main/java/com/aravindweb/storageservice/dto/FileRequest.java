package com.aravindweb.storageservice.dto;

import lombok.Getter;

@Getter
public class FileRequest {
    private String fileName;
    private String mime;
    private String objectName;
    private int expiry;
}
