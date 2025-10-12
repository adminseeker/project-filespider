package com.aravindweb.metadataservice.clients.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageServiceRequest {
    private String fileName;
    private String mime;
    private String objectName;
    private int expiry;
    private List<Integer> partNumbers;
    private String uploadId;
    private Map<Integer,String> partEtags;
}
