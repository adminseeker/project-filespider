package com.aravindweb.storageservice.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.SignedURLResponse;
import com.aravindweb.storageservice.exceptions.S3ClientCustomException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class DownloadService {
    @Autowired
    S3Client s3Client;

    @Autowired
    S3Presigner s3Presigner;

    @Value("${s3.bucketName}")
    private String bucketName;

    /**
     * Generate a presigned GET URL for downloading an existing object.
     * You must provide the bucket and objectName (from upload).
     */
    public SignedURLResponse generatePresignedGetUrl(FileRequest fileRequest){
        try {
            GetObjectRequest.Builder getObjectRequestBuilder = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileRequest.getObjectName());

            if(StringUtils.hasText(fileRequest.getFileName())){
                getObjectRequestBuilder.responseContentDisposition("attachment; filename=\"" + fileRequest.getFileName() + "\"");
            }
            if(StringUtils.hasText(fileRequest.getMime())){
                getObjectRequestBuilder.responseContentType(fileRequest.getMime());
            }
        
            PresignedGetObjectRequest presignedRequest =
                    s3Presigner.presignGetObject(r -> r
                            .signatureDuration(Duration.ofSeconds(fileRequest.getExpiry()))
                            .getObjectRequest(getObjectRequestBuilder.build())
                            );
                    
            return new SignedURLResponse(presignedRequest.url().toString());
        } catch (Exception e) {
            throw new S3ClientCustomException("Error Fetching Download URL from Storage Client!");
        }
        
    }
}
