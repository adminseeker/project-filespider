package com.aravindweb.metadataservice.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.clients.dto.StorageServiceRequest;
import com.aravindweb.metadataservice.clients.dto.StorageServiceResponse;
import com.aravindweb.metadataservice.dto.FileDownloadRequest;
import com.aravindweb.metadataservice.dto.FileDownloadResponse;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.exceptions.FileNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.exceptions.StorageServiceClientException;
import com.aravindweb.metadataservice.repos.FileMetaDataRepository;

@Service
public class DownloadService {
    
    @Autowired
    FileMetaDataRepository fileRepo;

    @Autowired
    StorageServiceClient storageServiceClient;


    public FileDownloadResponse fileDownload(FileDownloadRequest fileDownloadRequest, String userId){
        if(fileDownloadRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(fileDownloadRequest.getFileId(), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Not Found!"));
        
        if(!fileMetaData.getStatus().equals("S")) throw new FileNotFoundException("File Not Found!");

        StorageServiceResponse storageServiceResponse = storageServiceClient.getDownloadUrl(
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
