package com.aravindweb.storageservice.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FileRequest {
    private String fileName;
    private String mime;
    private String objectName;
    private int expiry;
    private List<Integer> partNumbers;
    private String uploadId;
}
