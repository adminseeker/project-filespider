package com.aravindweb.storageservice.services;


import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.MultiSignedPartsResponse;
import com.aravindweb.storageservice.dto.MultiUploadCompleteResponse;
import com.aravindweb.storageservice.dto.MultiUploadInitResponse;
import com.aravindweb.storageservice.exceptions.S3ClientCustomException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;



@Service
public class MultiUploadService {

    @Autowired
    S3Client s3Client;

    @Autowired
    S3Presigner s3Presigner;

    @Value("${s3.bucketName}")
    private String bucketName;
    
    /**
     * create multipart upload.
     */
    public MultiUploadInitResponse multipartUploadInit(FileRequest fileRequest){
        try {
            CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileRequest.getObjectName())
                .build();

            CreateMultipartUploadResponse resp = s3Client.createMultipartUpload(multipartUploadRequest);
            String uploadId = resp.uploadId();
            return MultiUploadInitResponse.builder()
                        .uploadId(uploadId)
                        .build();        

        } catch (Exception e) {
            throw new S3ClientCustomException("Error Fetching PUT URLs from Storage Client!");
        }
            
    }

    /**
     * Generate Presigned urls for the parts (file chunks).
     */
    public MultiSignedPartsResponse generatePresignedPartsPutUrl(FileRequest fileRequest){
        try {
            String uploadId = fileRequest.getUploadId();
            Map<Integer,String> presignedUrls = new LinkedHashMap<>();
            List<Integer> parts = fileRequest.getPartNumbers();
            for(int i : parts){
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(fileRequest.getObjectName())
                    .uploadId(uploadId)
                    .partNumber(i)
                    .build();

                UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest)
                    .signatureDuration(Duration.ofSeconds(fileRequest.getExpiry())) // e.g. Duration.ofMinutes(15)
                    .build();

                PresignedUploadPartRequest presigned = s3Presigner.presignUploadPart(presignRequest);

                presignedUrls.put(i, presigned.url().toString());
            }
            return MultiSignedPartsResponse.builder()
                        .uploadId(uploadId)
                        .urls(presignedUrls)
                        .build();        

        } catch (Exception e) {
            throw new S3ClientCustomException("Error Fetching PUT URLs from Storage Client!");
        }
            
    }

    /**
     * Complete Multi Part Upload.
     */

    public MultiUploadCompleteResponse completeMultipart(FileRequest fileRequest) {

        try {
            List<CompletedPart> completedParts = fileRequest.getPartEtags().entrySet().stream()
                                                .map((item)->
                                                    CompletedPart.builder()
                                                        .partNumber(item.getKey())
                                                        .eTag(item.getValue())
                                                        .build()
                                                )
                                                .collect(Collectors.toList());

            CompletedMultipartUpload multipart = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

            CompleteMultipartUploadRequest completeReq = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileRequest.getObjectName())
                .uploadId(fileRequest.getUploadId())
                .multipartUpload(multipart)
                .build();

            CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeReq);
            String status="";
            status = response.sdkHttpResponse().isSuccessful() ? "S" : "F";
            return MultiUploadCompleteResponse.builder().status(status).build();
        } catch (Exception e) {
            throw new S3ClientCustomException("Error Completing MultiPart Upload from Storage Client!");
        }
    }

    /**
     * Complete Multi Part Upload.
     */

    public void abortUpload(FileRequest fileRequest){
        try {
            AbortMultipartUploadRequest req = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileRequest.getObjectName())
                .uploadId(fileRequest.getUploadId())
                .build();
            AbortMultipartUploadResponse resp = s3Client.abortMultipartUpload(req);
            if(resp==null || resp.sdkHttpResponse()==null || !resp.sdkHttpResponse().isSuccessful()) 
            throw new S3ClientCustomException("Aborting Multipart Upload Failed!");
        } catch (Exception e) {
            throw new S3ClientCustomException("Aborting Multipart Upload Failed!");
        }
    }

}
