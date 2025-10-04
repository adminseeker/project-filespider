package com.aravindweb.metadataservice.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.clients.dto.StorageServiceRequest;
import com.aravindweb.metadataservice.clients.dto.StorageServiceResponse;
import com.aravindweb.metadataservice.dto.FileDownloadRequest;
import com.aravindweb.metadataservice.dto.FileDownloadResponse;
import com.aravindweb.metadataservice.dto.FileUploadCompleteRequest;
import com.aravindweb.metadataservice.dto.FileUploadCompleteResponse;
import com.aravindweb.metadataservice.dto.FileUploadInitResponse;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.exceptions.FileNotFoundException;
import com.aravindweb.metadataservice.exceptions.FolderNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.exceptions.StorageServiceClientException;
import com.aravindweb.metadataservice.repos.FileMetaDataRepository;
import com.aravindweb.metadataservice.repos.FolderMetaDataRepository;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@Service
public class SingleUploadService {
    @Autowired
    FolderMetaDataRepository folderRepo;

    @Autowired
    FileMetaDataRepository fileRepo;

    @Autowired
    MetaDataValidation metaDataValidation;

    @Autowired
    StorageServiceClient storageServiceClient;

    public FileUploadInitResponse fileUploadInit(FileMetaData fileMetaData){
        
        if(fileMetaData.getFolderId()!=null){
            folderRepo.findByFolderIdAndOwnerId(fileMetaData.getFolderId(), fileMetaData.getOwnerId()).orElseThrow(()->new FolderNotFoundException("Folder Doesn't Exist!"));
        }
        String objectKey = UUID.randomUUID().toString();
        fileMetaData.setObjectKey(objectKey);
        StorageServiceRequest storageServiceRequest = StorageServiceRequest.builder()
                                                        .fileName(fileMetaData.getFileName())
                                                        .objectName(fileMetaData.getObjectKey())
                                                        .expiry(3000)
                                                        .build();
        StorageServiceResponse storageServiceResponse = storageServiceClient.getSingleUploadUrl(storageServiceRequest)
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        fileMetaData.setStatus("P");
        fileRepo.save(fileMetaData);
        return FileUploadInitResponse.builder().url(storageServiceResponse.getUrl()).fileId(fileMetaData.getFileId().toString()).build();        
    }

    public FileUploadCompleteResponse fileUploadComplete(FileUploadCompleteRequest fileUploadCompleteRequest, String userId){
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(fileUploadCompleteRequest.getFileId(), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Metadata Not Found!"));
        String etag = fileUploadCompleteRequest.getEtag();
        if(etag==null) throw new InvalidFieldException("No Etag Passed!");
        StorageServiceResponse storageServiceResponse = storageServiceClient.dataValidation(StorageServiceRequest.builder().objectName(fileMetaData.getObjectKey()).build())
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        if(!StringUtils.hasText(etag)) fileMetaData.setStatus("I");
        if(etag.equals(storageServiceResponse.getEtag())) fileMetaData.setStatus("S");
        else fileMetaData.setStatus("I"); 
        fileRepo.save(fileMetaData);                                                
        return FileUploadCompleteResponse.builder().status(fileMetaData.getStatus()).build();
    }
    
    public FileDownloadResponse fileDownload(FileDownloadRequest fileDownloadRequest, String userId){
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(fileDownloadRequest.getFileId(), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Not Found!"));
        
        if(!fileMetaData.getStatus().equals("S")) throw new FileNotFoundException("File Not Found!");

        StorageServiceResponse storageServiceResponse = storageServiceClient.getSingleDownloadUrl(
                                                            StorageServiceRequest.builder()
                                                            .objectName(fileMetaData.getObjectKey())
                                                            .fileName(fileMetaData.getFileName())
                                                            .mime(fileMetaData.getMime())
                                                            .expiry(3000)
                                                            .build()
                                                        )
                                                        .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
                                                        
        return FileDownloadResponse.builder().url(storageServiceResponse.getUrl()).build();
    }

}
