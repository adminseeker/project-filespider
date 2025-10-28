package com.aravindweb.metadataservice.dto;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadCompleteRequest {
    private UUID fileId;
    private Map<Integer,String> partEtags;
    private String uploadId;
    private boolean multiPartUpload;
}
