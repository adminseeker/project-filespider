package com.aravindweb.storageservice.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.storageservice.dto.DataValidationRequest;
import com.aravindweb.storageservice.dto.DataValidationResponse;
import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.SignedURLResponse;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
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
    public SignedURLResponse generatePresignedPutUrl(FileRequest fileRequest) throws Exception {
 
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(bucketName)
                .object(fileRequest.getObjectName())
                .expiry(fileRequest.getExpiry())
                .build();

        String url = minioClient.getPresignedObjectUrl(args);
        return new SignedURLResponse(url);
    }

    /**
     * Generate a presigned GET URL for downloading an existing object.
     * You must provide the bucket and objectName (from upload).
     */
    public SignedURLResponse generatePresignedGetUrl(FileRequest fileRequest) throws Exception {
        Map<String, String> reqParams = new HashMap<>();
        if(StringUtils.hasText(fileRequest.getFileName())){
            reqParams.put("response-content-disposition", "attachment; filename=\"" + fileRequest.getFileName() + "\"");
        }
        if(StringUtils.hasText(fileRequest.getMime())){
            reqParams.put("response-content-type", fileRequest.getMime());
        }
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileRequest.getObjectName())
                .expiry(fileRequest.getExpiry())
                .extraQueryParams(reqParams)
                .build();

        return new SignedURLResponse(minioClient.getPresignedObjectUrl(args));
    }

    public DataValidationResponse getDataValidation(DataValidationRequest dataValidationRequest) throws Exception{

        StatObjectResponse stat = minioClient.statObject(
                                        StatObjectArgs.builder()
                                            .bucket(bucketName)
                                            .object(dataValidationRequest.getObjectName())
                                            .build()
                                    );
        return DataValidationResponse.builder().etag(stat.etag()).build();

    }

}
