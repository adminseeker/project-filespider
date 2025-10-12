package com.aravindweb.metadataservice.dto;

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
public class FileUploadInitResponse {
    private boolean multiPartUpload;
    private String fileId;
    private String uploadId;
    private Integer totalParts;
}
