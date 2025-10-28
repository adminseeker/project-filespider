package com.aravindweb.storageservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.storageservice.dto.DataValidationRequest;
import com.aravindweb.storageservice.dto.DataValidationResponse;
import com.aravindweb.storageservice.dto.ErrorResponse;
import com.aravindweb.storageservice.dto.FileRequest;
import com.aravindweb.storageservice.dto.SignedURLResponse;
import com.aravindweb.storageservice.exceptions.StorageServiceException;
import com.aravindweb.storageservice.services.SingleUploadService;

@RestController
@RequestMapping("/api/v1/storage/privateapi/single")
public class SingleFileUploadController {
    
    @Autowired
    SingleUploadService singleUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> getPresignedUploadUrl(@RequestBody FileRequest fileRequest){
        try {
            ResponseEntity<SignedURLResponse> signedUrlResp = new ResponseEntity<SignedURLResponse>(singleUploadService.generatePresignedPutUrl(fileRequest),HttpStatus.OK);
            return signedUrlResp;
        } catch (StorageServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateData(@RequestBody DataValidationRequest dataValidationRequest){
        try {
            ResponseEntity<DataValidationResponse> signedUrlResp = new ResponseEntity<DataValidationResponse>(singleUploadService.getDataValidation(dataValidationRequest),HttpStatus.OK);
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
