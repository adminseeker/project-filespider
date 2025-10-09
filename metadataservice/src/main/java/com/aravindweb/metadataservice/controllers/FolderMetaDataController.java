package com.aravindweb.metadataservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.metadataservice.dto.ErrorResponse;
import com.aravindweb.metadataservice.entities.FolderMetaData;
import com.aravindweb.metadataservice.exceptions.MetaDataServiceException;
import com.aravindweb.metadataservice.services.MetaDataService;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@RestController
@RequestMapping("/api/v1/metadata/folders")
public class FolderMetaDataController {

    @Autowired
    MetaDataService metaDataService;

    @Autowired
    MetaDataValidation metaDataValidation;
    
    @PostMapping("")
    public ResponseEntity<?> createFolder(@RequestHeader HttpHeaders headers, @RequestBody FolderMetaData folderMetadata){
        try {
            metaDataValidation.validateCreateFolder(folderMetadata, headers);
            ResponseEntity<FolderMetaData> folderMetadataResp = new ResponseEntity<FolderMetaData>(metaDataService.createFolder(folderMetadata),HttpStatus.CREATED);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFolders(@RequestHeader HttpHeaders headers){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<List<FolderMetaData>> folderMetadataResp = new ResponseEntity<List<FolderMetaData>>(metaDataService.getAllFolders(userId),HttpStatus.OK);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<?> getFolderById(@RequestHeader HttpHeaders headers, @PathVariable String folderId){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<FolderMetaData> folderMetadataResp = new ResponseEntity<FolderMetaData>(metaDataService.getFolderById(folderId, userId),HttpStatus.OK);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{folderId}")
    public ResponseEntity<?> updateFolderById(@RequestHeader HttpHeaders headers, @PathVariable String folderId, @RequestBody FolderMetaData folderMetaData){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<FolderMetaData> folderMetadataResp = new ResponseEntity<FolderMetaData>(metaDataService.updateFolderById(folderId, userId, folderMetaData),HttpStatus.OK);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<?> deleteFolderById(@RequestHeader HttpHeaders headers, @PathVariable String folderId){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<FolderMetaData> folderMetadataResp = new ResponseEntity<FolderMetaData>(metaDataService.deleteFolderById(folderId, userId),HttpStatus.OK);
            return folderMetadataResp;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }



}
