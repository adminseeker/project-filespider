package com.aravindweb.metadataservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.metadataservice.dto.ErrorResponse;
import com.aravindweb.metadataservice.dto.ItemsListing;
import com.aravindweb.metadataservice.exceptions.MetaDataServiceException;
import com.aravindweb.metadataservice.services.MetaDataService;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@RestController
@RequestMapping("/api/v1/metadata/files")
public class FileMetaDataController {

    @Autowired
    MetaDataService metaDataService;

    @Autowired
    MetaDataValidation metaDataValidation;
    

    @GetMapping("/root")
    public ResponseEntity<?> getAllFilesUnderRoot(@RequestHeader HttpHeaders headers){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<List<ItemsListing>> files = new ResponseEntity<List<ItemsListing>>(metaDataService.getItemsByFolderId("", userId, true),HttpStatus.OK);
            return files;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<?> getAllFilesByFolderId(@RequestHeader HttpHeaders headers, @PathVariable String folderId){
        try {
            String userId = metaDataValidation.validateXUserId(headers);
            ResponseEntity<List<ItemsListing>> files = new ResponseEntity<List<ItemsListing>>(metaDataService.getItemsByFolderId(folderId, userId, false),HttpStatus.OK);
            return files;
        } catch (MetaDataServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }
}
