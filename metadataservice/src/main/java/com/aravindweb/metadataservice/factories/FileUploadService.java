package com.aravindweb.metadataservice.factories;

import com.aravindweb.metadataservice.dto.AbortUploadRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteResponse;
import com.aravindweb.metadataservice.dto.FileUploadInitResponse;
import com.aravindweb.metadataservice.dto.FileUploadRequest;
import com.aravindweb.metadataservice.dto.PresignUrlRequest;
import com.aravindweb.metadataservice.dto.PresignUrlResponse;

public interface FileUploadService {
    public FileUploadInitResponse fileUploadInit(FileUploadRequest fileUploadRequest, String ownerId);
    public PresignUrlResponse presignUrl(PresignUrlRequest presignUrlRequest, String ownerId);
    public FileUploadCompleteResponse fileUploadComplete(FileUploadCompleteRequest fileUploadCompleteRequest, String userId);
    default void abortMultiPartUpload(AbortUploadRequest abortUploadRequest, String userId){};
}
