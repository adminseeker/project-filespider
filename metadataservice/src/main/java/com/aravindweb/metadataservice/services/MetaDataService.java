package com.aravindweb.metadataservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.dto.ItemsListing;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.entities.FolderMetaData;
import com.aravindweb.metadataservice.exceptions.FolderNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.repos.FileMetaDataRepository;
import com.aravindweb.metadataservice.repos.FolderMetaDataRepository;
import com.aravindweb.metadataservice.utils.MetaDataValidation;

@Service
public class MetaDataService {
    
    @Autowired
    FolderMetaDataRepository folderRepo;

    @Autowired
    FileMetaDataRepository fileRepo;

    @Autowired
    MetaDataValidation metaDataValidation;

    @Autowired
    StorageServiceClient storageServiceClient;

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

    public List<ItemsListing> getItemsByFolderId(String folderId, String ownerId, boolean isRoot){
        List<FolderMetaData> foldersMetaData = new ArrayList<>();
        List<FileMetaData> filesMetaData = new ArrayList<>();
        if(isRoot){
            foldersMetaData = folderRepo.findByParentFolderIsNullAndOwnerId(UUID.fromString(ownerId))
                                    .orElse(new ArrayList<>());
            filesMetaData = fileRepo.findByFolderIdIsNullAndOwnerId(UUID.fromString(ownerId))
                                    .orElse(new ArrayList<>());
        }else{
            foldersMetaData = folderRepo.findByParentFolderAndOwnerId(UUID.fromString(folderId), UUID.fromString(ownerId))
                                                    .orElse(new ArrayList<>());
            filesMetaData = fileRepo.findByFolderIdAndOwnerId(UUID.fromString(folderId), UUID.fromString(ownerId))
                                                    .orElse(new ArrayList<>());
        }

        List<ItemsListing> folders = foldersMetaData.stream()
            .map((folder)-> ItemsListing.builder()
                .itemId(folder.getFolderId())
                .isFolder(true)
                .itemName(folder.getFolderName())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .ownerId(folder.getOwnerId())
                .parentId(folder.getParentFolder())
                .build()
        ).collect(Collectors.toList());

        List<ItemsListing> files = filesMetaData.stream()
            .map((file)-> ItemsListing.builder()
                .itemId(file.getFileId())
                .itemName(file.getFileName())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .ownerId(file.getOwnerId())
                .parentId(file.getFolderId())
                .mime(file.getMime())
                .fileSize(file.getFileSize())
                .build()
        ).collect(Collectors.toList());

        files.addAll(folders);
        return files;
    }

}
