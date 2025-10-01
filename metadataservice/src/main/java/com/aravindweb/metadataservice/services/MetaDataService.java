package com.aravindweb.metadataservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.entities.FolderMetaData;
import com.aravindweb.metadataservice.exceptions.FolderNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.repos.FolderMetaDataRepository;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@Service
public class MetaDataService {
    
    @Autowired
    FolderMetaDataRepository folderRepo;

    @Autowired
    MetaDataValidation metaDataValidation;

    public FolderMetaData createFolder(FolderMetaData folderMetaData){
        folderRepo.save(folderMetaData);
        return folderMetaData;
    }

    public List<FolderMetaData> getAllFolders(String ownerId){
        return folderRepo.findByOwnerId(UUID.fromString(ownerId)).orElse(new ArrayList<>());
    }

    public FolderMetaData getFolderById(String folderId, String ownerId){
        return folderRepo.findByFolderIdAndOwnerId(UUID.fromString(folderId), UUID.fromString(ownerId)).orElseThrow(()->new FolderNotFoundException("Folder Not Found!"));
    }

    public FolderMetaData updateFolderById(String folderId, String ownerId, FolderMetaData folderMetaDataRequest){
        FolderMetaData folderMetaDataDb = getFolderById(folderId, ownerId);
        if(folderMetaDataRequest==null || !StringUtils.hasText(folderMetaDataRequest.getFolderName())) throw new InvalidFieldException("Invalid Field!");
        folderMetaDataDb.setFolderName(folderMetaDataRequest.getFolderName());
        return folderRepo.save(folderMetaDataDb);
    }

    public FolderMetaData deleteFolderById(String folderId, String ownerId){
        FolderMetaData folderMetaDataDb = getFolderById(folderId, ownerId);
        folderRepo.delete(folderMetaDataDb);
        return folderMetaDataDb;
    }
}
