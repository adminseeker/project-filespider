package com.aravindweb.metadataservice.clients.dto;

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
}
