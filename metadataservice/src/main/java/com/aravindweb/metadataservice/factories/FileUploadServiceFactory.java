package com.aravindweb.metadataservice.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aravindweb.metadataservice.services.MultiUploadService;
import com.aravindweb.metadataservice.services.SingleUploadService;

@Component
public class FileUploadServiceFactory {
    
    @Autowired
    SingleUploadService singleUploadService;

    @Autowired
    MultiUploadService multiUploadService;

    private final long MULTIUPLOAD_THRESHOLD = 20L * 1024L * 1024L; //20MB

    public FileUploadService getFileUploadService(long fileSize){
        return fileSize <= MULTIUPLOAD_THRESHOLD ? singleUploadService : multiUploadService;
    }

    public FileUploadService getFileUploadService(boolean multiPartUpload){
        return multiPartUpload ? multiUploadService : singleUploadService;
    }

}
