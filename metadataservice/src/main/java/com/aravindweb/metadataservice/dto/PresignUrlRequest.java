package com.aravindweb.metadataservice.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PresignUrlRequest {
    private String fileId;
    private String uploadId;
    private boolean multiPartUpload;
    private List<Integer> parts;
}
