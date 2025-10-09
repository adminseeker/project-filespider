package com.aravindweb.storageservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;




import software.amazon.awssdk.services.s3.S3Client;



@Service
public class MultiUploadService {

    @Autowired
    S3Client s3Client;

    @Value("${s3.bucketName}")
    private String bucketName;
    
    /**
     * Generate Presigned urls for the parts (file chunks).
     */
    // public MultiSignedPartsResponse generatePresignedPartsPutUrl(FileRequest fileRequest) throws Exception {

        
 
     
    // }

}
