package com.aravindweb.storageservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.storageservice.dto.ErrorResponse;
import com.aravindweb.storageservice.dto.SignedURLResponse;
import com.aravindweb.storageservice.exceptions.StorageServiceException;
import com.aravindweb.storageservice.services.SingleUploadService;

@RestController
@RequestMapping("/api/v1/storage/privateapi")
public class SingleFileUploadController {
    
    @Autowired
    SingleUploadService singleUploadService;

    @GetMapping("/upload/{fileName}")
    public ResponseEntity<?> getPresignedUploadUrl(@RequestHeader HttpHeaders headers, @PathVariable String fileName){
        try {
            ResponseEntity<SignedURLResponse> signedUrlResp = new ResponseEntity<SignedURLResponse>(singleUploadService.generatePresignedPutUrl(fileName),HttpStatus.OK);
            return signedUrlResp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> getPresignedDownloadUrl(@RequestHeader HttpHeaders headers, @PathVariable String fileName){
        try {
            ResponseEntity<SignedURLResponse> signedUrlResp = new ResponseEntity<SignedURLResponse>(singleUploadService.generatePresignedGetUrl(fileName),HttpStatus.OK);
            return signedUrlResp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }
}
