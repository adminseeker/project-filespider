package com.aravindweb.metadataservice.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.clients.dto.StorageServiceRequest;
import com.aravindweb.metadataservice.clients.dto.StorageServiceResponse;
import com.aravindweb.metadataservice.dto.AbortUploadRequest;
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
import com.aravindweb.metadataservice.repos.FileDeleteTrackerRepository;
import com.aravindweb.metadataservice.repos.FileMetaDataRepository;
import com.aravindweb.metadataservice.repos.FolderMetaDataRepository;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@Service
public class MultiUploadService implements FileUploadService{

    @Autowired
    FolderMetaDataRepository folderRepo;
    
    @Autowired
    FileMetaDataRepository fileRepo;

    @Autowired
    FileDeleteTrackerRepository deletedFilesRepo;

    @Autowired
    MetaDataValidation metaDataValidation;

    @Autowired        
    MetaDataService metaDataService;

    @Autowired
    StorageServiceClient storageServiceClient;

    private final long DEFAULT_PART_SIZE= 10L * 1024L * 1024L; //10MB
    private final long MIN_PART_SIZE= 5L * 1024L * 1024L; //5MB
    private final long MAX_PART_SIZE= 100L * 1024L * 1024L; //100MB
    private final int MAX_PARTS = 10_000;

    @Override
    public FileUploadInitResponse fileUploadInit(FileUploadRequest fileUploadRequest, String ownerId){
        
        FileMetaData fileMetaData = new FileMetaData();
        if(StringUtils.hasText(fileUploadRequest.getFolderId())){
            folderRepo.findByFolderIdAndOwnerId(UUID.fromString(fileUploadRequest.getFolderId()), UUID.fromString(ownerId)).orElseThrow(()->new FolderNotFoundException("Folder Doesn't Exist!"));
            fileMetaData.setFolderId(UUID.fromString(fileUploadRequest.getFolderId()));
        }
        if(fileUploadRequest.getFileSize()==0L) throw new InvalidFieldException("File size cannot be zero!");
        String objectKey = UUID.randomUUID().toString();
        long partSize = (fileUploadRequest.getPartSize() == 0L)
            ? DEFAULT_PART_SIZE // default 16MB
            : Math.max(MIN_PART_SIZE, Math.min(MAX_PART_SIZE, fileUploadRequest.getPartSize()));
        int totalParts = (int) ((fileUploadRequest.getFileSize() + partSize - 1) / partSize);
        if(totalParts>MAX_PARTS) throw new InvalidFieldException("file too large for multipart upload with the given part size!");

        StorageServiceRequest storageServiceRequest = StorageServiceRequest.builder()
                                                        .objectName(objectKey)
                                                        .build();
        StorageServiceResponse storageServiceResponse = storageServiceClient.multiUploadInit(storageServiceRequest).orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        
        fileMetaData.setFileName(fileUploadRequest.getFileName());
        fileMetaData.setOwnerId(UUID.fromString(ownerId));
        fileMetaData.setMime(fileUploadRequest.getMime());
        fileMetaData.setFileSize(fileUploadRequest.getFileSize());
        fileMetaData.setObjectKey(objectKey);
        fileMetaData.setStatus("P");
        fileRepo.save(fileMetaData);

        return FileUploadInitResponse.builder()
                .multiPartUpload(true)
                .fileId(fileMetaData.getFileId().toString())
                .uploadId(storageServiceResponse.getUploadId())
                .totalParts(totalParts)
                .build();
    }

    @Override
    public PresignUrlResponse presignUrl(PresignUrlRequest presignUrlRequest, String ownerId){
        
        if(presignUrlRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(UUID.fromString(presignUrlRequest.getFileId()), UUID.fromString(ownerId)).orElseThrow(()-> new FileNotFoundException("File Metadata Not Found!"));
        StorageServiceRequest storageServiceRequest = StorageServiceRequest.builder()
                                                        .fileName(fileMetaData.getFileName())
                                                        .objectName(fileMetaData.getObjectKey())
                                                        .partNumbers(presignUrlRequest.getParts())
                                                        .uploadId(presignUrlRequest.getUploadId())
                                                        .expiry(3000)
                                                        .build();
        StorageServiceResponse storageServiceResponse = storageServiceClient.getMultiUploadPartsSignedUrls(storageServiceRequest)
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));
        return PresignUrlResponse.builder()
                    .partsUrls(storageServiceResponse.getUrls())
                    .multiPartUpload(true)
                    .uploadId(presignUrlRequest.getUploadId())
                    .fileId(presignUrlRequest.getFileId())
                    .build();
    }

    @Override
    public FileUploadCompleteResponse fileUploadComplete(FileUploadCompleteRequest fileUploadCompleteRequest, String userId){
        if(fileUploadCompleteRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        if(fileUploadCompleteRequest.getUploadId()==null) throw new InvalidFieldException("uploadId is required!");
        Map<Integer,String> partEtags = fileUploadCompleteRequest.getPartEtags();
        if(partEtags==null || partEtags.size()==0) throw new InvalidFieldException("No Etags Passed!");


        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(fileUploadCompleteRequest.getFileId(), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Metadata Not Found!"));
       
       
        StorageServiceResponse storageServiceResponse = storageServiceClient.completeMultiPartUpload(StorageServiceRequest.builder()
                                                                .objectName(fileMetaData.getObjectKey())
                                                                .partEtags(partEtags)
                                                                .uploadId(fileUploadCompleteRequest.getUploadId())
                                                                .build()
                                                            )
                                                            .orElseThrow(()-> new StorageServiceClientException("Invalid Request!"));             
        fileMetaData.setStatus(storageServiceResponse.getStatus());
        fileRepo.save(fileMetaData);                                                
        return FileUploadCompleteResponse.builder().status(fileMetaData.getStatus()).build();
    }

    @Override
    public void abortMultiPartUpload(AbortUploadRequest abortUploadRequest, String userId){
        if(abortUploadRequest.getFileId()==null) throw new InvalidFieldException("FileId is required!");
        if(abortUploadRequest.getUploadId()==null) throw new InvalidFieldException("uploadId is required!");


        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(UUID.fromString(abortUploadRequest.getFileId()), UUID.fromString(userId))
                                        .orElseThrow(()->new FileNotFoundException("File Metadata Not Found!"));
       
        
        storageServiceClient.abortMultiPartUpload(StorageServiceRequest.builder()
                                                                .objectName(fileMetaData.getObjectKey())
                                                                .uploadId(abortUploadRequest.getUploadId())
                                                                .build()
                                                            );
                     
        
        metaDataService.deleteFileById(abortUploadRequest.getFileId(), userId);
    }

}
