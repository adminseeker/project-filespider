package com.aravindweb.metadataservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.metadataservice.dto.AbortUploadRequest;
import com.aravindweb.metadataservice.dto.ErrorResponse;
import com.aravindweb.metadataservice.dto.FileDownloadRequest;
import com.aravindweb.metadataservice.dto.FileDownloadResponse;
import com.aravindweb.metadataservice.dto.FileUploadCompleteRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteResponse;
import com.aravindweb.metadataservice.dto.FileUploadInitResponse;
import com.aravindweb.metadataservice.dto.FileUploadRequest;
import com.aravindweb.metadataservice.dto.PresignUrlRequest;
import com.aravindweb.metadataservice.dto.PresignUrlResponse;
import com.aravindweb.metadataservice.exceptions.MetaDataServiceException;
import com.aravindweb.metadataservice.factories.FileUploadService;
import com.aravindweb.metadataservice.factories.FileUploadServiceFactory;
import com.aravindweb.metadataservice.services.DownloadService;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@RestController
@RequestMapping("/api/v1/metadata/files")
public class FileUploadDownloadController {

    @Autowired
    FileUploadServiceFactory fileUploadServiceFactory;

    @Autowired
    DownloadService downloadService;

    @Autowired
    MetaDataValidation metaDataValidation;

    @PostMapping("/upload/init")
    public ResponseEntity<?> fileUploadInit(@RequestHeader HttpHeaders headers, @RequestBody FileUploadRequest fileUploadRequest){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            metaDataValidation.validateFileUploadRequestInit(fileUploadRequest);
            FileUploadService fileUploadService = fileUploadServiceFactory.getFileUploadService(fileUploadRequest.getFileSize());
            ResponseEntity<FileUploadInitResponse> fileUploadInitResp = new ResponseEntity<FileUploadInitResponse>(fileUploadService.fileUploadInit(fileUploadRequest,userId),HttpStatus.CREATED);
            return fileUploadInitResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload/sign")
    public ResponseEntity<?> getSignedUrl(@RequestHeader HttpHeaders headers, @RequestBody PresignUrlRequest presignUrlRequest){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            FileUploadService fileUploadService = fileUploadServiceFactory.getFileUploadService(presignUrlRequest.isMultiPartUpload());
            ResponseEntity<PresignUrlResponse> resp = new ResponseEntity<PresignUrlResponse>(fileUploadService.presignUrl(presignUrlRequest,userId),HttpStatus.CREATED);
            return resp;
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
            FileUploadService fileUploadService = fileUploadServiceFactory.getFileUploadService(fileUploadCompleteRequest.isMultiPartUpload());
            ResponseEntity<FileUploadCompleteResponse> folderMetadataResp = new ResponseEntity<FileUploadCompleteResponse>(fileUploadService.fileUploadComplete(fileUploadCompleteRequest,userId),HttpStatus.CREATED);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload/abort")
    public ResponseEntity<?> abortUpload(@RequestHeader HttpHeaders headers, @RequestBody AbortUploadRequest abortUploadRequest){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            FileUploadService fileUploadService = fileUploadServiceFactory.getFileUploadService(true);
            fileUploadService.abortMultiPartUpload(abortUploadRequest,userId);
            return ResponseEntity.noContent().build();
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
            ResponseEntity<FileDownloadResponse> fileDownloadInitResp = new ResponseEntity<FileDownloadResponse>(downloadService.fileDownload(fileDownloadRequest,userId),HttpStatus.CREATED);
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
