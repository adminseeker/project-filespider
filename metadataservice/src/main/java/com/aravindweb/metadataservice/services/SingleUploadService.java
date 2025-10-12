package com.aravindweb.metadataservice.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.clients.dto.StorageServiceRequest;
import com.aravindweb.metadataservice.clients.dto.StorageServiceResponse;
import com.aravindweb.metadataservice.dto.FileUploadCompleteRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteResponse;
import com.aravindweb.metadataservice.dto.FileUploadInitResponse;
import com.aravindweb.metadataservice.dto.FileUploadRequest;
import com.aravindweb.metadataservice.dto.PresignUrlRequest;
import com.aravindweb.metadataservice.dto.PresignUrlResponse;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.exceptions.FileNotFoundException;
import com.aravindweb.metadataservice.exceptions.FolderNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.exceptions.StorageServiceClientException;
import com.aravindweb.metadataservice.factories.FileUploadService;
import com.aravindweb.metadataservice.repos.FileMetaDataRepository;
import com.aravindweb.metadataservice.repos.FolderMetaDataRepository;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@Service
public class SingleUploadService implements FileUploadService{
    @Autowired
    FolderMetaDataRepository folderRepo;

    @Autowired
    FileMetaDataRepository fileRepo;

    @Autowired
    MetaDataValidation metaDataValidation;

    @Autowired
    StorageServiceClient storageServiceClient;

    @Override
    public FileUploadInitResponse fileUploadInit(FileUploadRequest fileUploadRequest,String ownerId){
        
        FileMetaData fileMetaData = new FileMetaData();
        if(StringUtils.hasText(fileUploadRequest.getFolderId())){
            folderRepo.findByFolderIdAndOwnerId(UUID.fromString(fileUploadRequest.getFolderId()), UUID.fromString(ownerId)).orElseThrow(()->new FolderNotFoundException("Folder Doesn't Exist!"));
            fileMetaData.setFolderId(UUID.fromString(fileUploadRequest.getFolderId()));
        }
        
        fileMetaData.setOwnerId(UUID.fromString(ownerId));
        fileMetaData.setMime(fileUploadRequest.getMime());
        fileMetaData.setFileSize(fileUploadRequest.getFileSize());
        fileMetaData.setFileName(fileUploadRequest.getFileName());
        String objectKey = UUID.randomUUID().toString();
        fileMetaData.setObjectKey(objectKey);
        fileMetaData.setStatus("P");
        fileRepo.save(fileMetaData);
        return FileUploadInitResponse.builder().fileId(fileMetaData.getFileId().toString()).build();        
    }

    @Override
    public PresignUrlResponse presignUrl(PresignUrlRequest presignUrlRequest, String ownerId){
        
        if(presignUrlRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(UUID.fromString(presignUrlRequest.getFileId()), UUID.fromString(ownerId)).orElseThrow(()-> new FileNotFoundException("File Metadata Not Found!"));
        StorageServiceRequest storageServiceRequest = StorageServiceRequest.builder()
                                                        .fileName(fileMetaData.getFileName())
                                                        .objectName(fileMetaData.getObjectKey())
                                                        .expiry(3000)
                                                        .build();
        StorageServiceResponse storageServiceResponse = storageServiceClient.getSingleUploadUrl(storageServiceRequest)
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        Map<Integer, String> signedUrls = new LinkedHashMap<>();
        signedUrls.put(1, storageServiceResponse.getUrl());
        return PresignUrlResponse.builder()
                    .partsUrls(signedUrls)
                    .fileId(presignUrlRequest.getFileId())
                    .build();
    }
    
    @Override
    public FileUploadCompleteResponse fileUploadComplete(FileUploadCompleteRequest fileUploadCompleteRequest, String userId){
        if(fileUploadCompleteRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(fileUploadCompleteRequest.getFileId(), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Metadata Not Found!"));
        Map<Integer,String> partEtag = fileUploadCompleteRequest.getPartEtags();
        if(partEtag==null || !StringUtils.hasText(partEtag.get(1))) throw new InvalidFieldException("No Etag Passed!");
        String etag = partEtag.get(1);
        StorageServiceResponse storageServiceResponse = storageServiceClient.dataValidation(StorageServiceRequest.builder().objectName(fileMetaData.getObjectKey()).build())
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        if(!StringUtils.hasText(etag)) fileMetaData.setStatus("I");
        if(etag.equals(storageServiceResponse.getEtag())) fileMetaData.setStatus("S");
        else fileMetaData.setStatus("I"); 
        fileRepo.save(fileMetaData);                                                
        return FileUploadCompleteResponse.builder().status(fileMetaData.getStatus()).build();
    }

}
