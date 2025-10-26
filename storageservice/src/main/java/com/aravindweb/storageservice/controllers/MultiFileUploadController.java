package com.aravindweb.storageservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.storageservice.dto.ErrorResponse;
import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.MultiSignedPartsResponse;
import com.aravindweb.storageservice.dto.MultiUploadCompleteResponse;
import com.aravindweb.storageservice.dto.MultiUploadInitResponse;
import com.aravindweb.storageservice.exceptions.StorageServiceException;
import com.aravindweb.storageservice.services.MultiUploadService;

@RestController
@RequestMapping("/api/v1/storage/privateapi/multiupload")
public class MultiFileUploadController {
    
    @Autowired
    MultiUploadService multiUploadService;

    @PostMapping("/init")
    public ResponseEntity<?> multipartUploadInit(@RequestBody FileRequest fileRequest){
        try {
            ResponseEntity<MultiUploadInitResponse> resp = new ResponseEntity<MultiUploadInitResponse>(multiUploadService.multipartUploadInit(fileRequest),HttpStatus.OK);
            return resp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sign")
    public ResponseEntity<?> getPresignedUploadUrl(@RequestBody FileRequest fileRequest){
        try {
            ResponseEntity<MultiSignedPartsResponse> signedUrlResp = new ResponseEntity<MultiSignedPartsResponse>(multiUploadService.generatePresignedPartsPutUrl(fileRequest),HttpStatus.OK);
            return signedUrlResp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeUpload(@RequestBody FileRequest fileRequest){
        try {
            ResponseEntity<MultiUploadCompleteResponse> resp = new ResponseEntity<MultiUploadCompleteResponse>(multiUploadService.completeMultipart(fileRequest),HttpStatus.OK);
            return resp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/abort")
    public ResponseEntity<?> abortUpload(@RequestBody FileRequest fileRequest){
        try {
            multiUploadService.abortUpload(fileRequest);
            return ResponseEntity.noContent().build();
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }
}
