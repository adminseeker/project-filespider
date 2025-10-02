package com.aravindweb.storageservice.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aravindweb.storageservice.dto.SignedURLResponse;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;


@Service
public class SingleUploadService {

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;
    
    /**
     * Generate a presigned PUT URL and return objectName + URL.
     */
    public SignedURLResponse generatePresignedPutUrl(String originalFilename) throws Exception {
        String objectName = (originalFilename == null || originalFilename.isBlank())
                ? UUID.randomUUID().toString()
                : UUID.randomUUID().toString() + "-" + originalFilename;

        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(bucketName)
                .object(objectName)
                .expiry(3600)
                .build();

        String url = minioClient.getPresignedObjectUrl(args);
        return new SignedURLResponse(url);
    }

    /**
     * Generate a presigned GET URL for downloading an existing object.
     * You must provide the bucket and objectName (from upload).
     */
    public SignedURLResponse generatePresignedGetUrl(String objectName) throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(60)
                .build();

        return new SignedURLResponse(minioClient.getPresignedObjectUrl(args));
    }

}
