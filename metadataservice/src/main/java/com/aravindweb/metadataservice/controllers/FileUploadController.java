package com.aravindweb.metadataservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.metadataservice.dto.ErrorResponse;
import com.aravindweb.metadataservice.dto.FileDownloadRequest;
import com.aravindweb.metadataservice.dto.FileDownloadResponse;
import com.aravindweb.metadataservice.dto.FileUploadCompleteRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteResponse;
import com.aravindweb.metadataservice.dto.FileUploadInitResponse;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.exceptions.MetaDataServiceException;
import com.aravindweb.metadataservice.services.SingleUploadService;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@RestController
@RequestMapping("/api/v1/metadata/files")
public class FileUploadController {

    @Autowired
    SingleUploadService singleUploadService;

    @Autowired
    MetaDataValidation metaDataValidation;

    @PostMapping("/upload/init")
    public ResponseEntity<?> fileUploadInit(@RequestHeader HttpHeaders headers, @RequestBody FileMetaData fileMetaData){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            fileMetaData.setOwnerId(UUID.fromString(userId));
            metaDataValidation.validateFileMetadataInit(fileMetaData);
            ResponseEntity<FileUploadInitResponse> fileUploadInitResp = new ResponseEntity<FileUploadInitResponse>(singleUploadService.fileUploadInit(fileMetaData),HttpStatus.CREATED);
            return fileUploadInitResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload/complete")
    public ResponseEntity<?> fileUploadComplete(@RequestHeader HttpHeaders headers, @RequestBody FileUploadCompleteRequest fileUploadCompleteRequest){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<FileUploadCompleteResponse> folderMetadataResp = new ResponseEntity<FileUploadCompleteResponse>(singleUploadService.fileUploadComplete(fileUploadCompleteRequest,userId),HttpStatus.CREATED);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> fileDownload(@RequestHeader HttpHeaders headers, @RequestBody FileDownloadRequest fileDownloadRequest){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<FileDownloadResponse> fileDownloadInitResp = new ResponseEntity<FileDownloadResponse>(singleUploadService.fileDownload(fileDownloadRequest,userId),HttpStatus.CREATED);
            return fileDownloadInitResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

}
