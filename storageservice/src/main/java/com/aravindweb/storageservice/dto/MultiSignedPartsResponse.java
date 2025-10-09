package com.aravindweb.storageservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiSignedPartsResponse {
    private String uploadId;
    private Map<Integer, String> urls;
}
