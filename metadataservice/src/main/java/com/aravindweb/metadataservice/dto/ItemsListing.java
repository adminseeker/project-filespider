package com.aravindweb.metadataservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemsListing {
    private UUID itemId;
    private UUID parentId;
    private String itemName;
    private long fileSize;
    private String mime;
    private String version;
    private boolean isFolder;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
