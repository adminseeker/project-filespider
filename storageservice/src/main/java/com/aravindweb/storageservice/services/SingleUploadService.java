package com.aravindweb.storageservice.services;

import java.time.Duration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.storageservice.dto.DataValidationRequest;
import com.aravindweb.storageservice.dto.DataValidationResponse;
import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.SignedURLResponse;
import com.aravindweb.storageservice.exceptions.S3ClientCustomException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;


@Service
public class SingleUploadService {

    @Autowired
    S3Client s3Client;

    @Autowired
    S3Presigner s3Presigner;

    @Value("${s3.bucketName}")
    private String bucketName;
    
    /**
     * Generate a presigned PUT URL and return objectName + URL.
     */
    public SignedURLResponse generatePresignedPutUrl(FileRequest fileRequest) throws Exception {

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileRequest.getObjectName())
            .build();

            PresignedPutObjectRequest presignedRequest =
                    s3Presigner.presignPutObject(r -> r
                            .signatureDuration(Duration.ofSeconds(fileRequest.getExpiry()))
                            .putObjectRequest(putObjectRequest));
            return new SignedURLResponse(presignedRequest.url().toString());
        } catch (Exception e) {
            throw new S3ClientCustomException("Error Fetching PUT URL from Storage Client!");
        }
        
    }

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

    public DataValidationResponse getDataValidation(DataValidationRequest dataValidationRequest){
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(dataValidationRequest.getObjectName())
            .build();

            HeadObjectResponse headResponse = s3Client.headObject(headRequest);
            return DataValidationResponse.builder().etag(headResponse.eTag().replace("\"","")).build();
        } catch (Exception e) {
            throw new S3ClientCustomException("Error Fetching eTag Metadata from Storage Client!");
        }
    }
}
