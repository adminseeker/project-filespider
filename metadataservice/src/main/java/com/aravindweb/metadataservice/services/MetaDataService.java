package com.aravindweb.metadataservice.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.clients.StorageServiceClient;
import com.aravindweb.metadataservice.dto.ItemsListing;
import com.aravindweb.metadataservice.entities.FileDeleteTracker;
import com.aravindweb.metadataservice.entities.FileMetaData;
import com.aravindweb.metadataservice.entities.FolderMetaData;
import com.aravindweb.metadataservice.exceptions.FileNotFoundException;
import com.aravindweb.metadataservice.exceptions.FolderNotFoundException;
import com.aravindweb.metadataservice.exceptions.InvalidFieldException;
import com.aravindweb.metadataservice.repos.FileDeleteTrackerRepository;
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
    FileDeleteTrackerRepository deletedFilesRepo;

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

    @Transactional
    public FolderMetaData deleteFolderById(String folderId, String ownerId){
        FolderMetaData folderMetaDataDb = getFolderById(folderId, ownerId);
        ArrayDeque<String> foldersStack = new ArrayDeque<>();
        Set<String> objectKeys = new LinkedHashSet<>();
        Set<UUID> fileIds = new LinkedHashSet<>();

        foldersStack.push(folderId);
        while(!foldersStack.isEmpty()){
            String currentFolderId = foldersStack.pop();
            List<ItemsListing> items = getItemsByFolderId(currentFolderId, ownerId, false);
            if(items==null) continue;
            for(ItemsListing item : items){
                if(item.isFolder()) foldersStack.push(item.getItemId().toString());
                else{
                    objectKeys.add(item.getObjectKey());
                    fileIds.add(item.getItemId());
                }
            }
        }

        List<FileDeleteTracker> deletedFiles = objectKeys.stream()
                                                    .map((objectKey) -> 
                                                        FileDeleteTracker.builder()
                                                        .ownerId(UUID.fromString(ownerId))
                                                        .objectKey(objectKey)
                                                        .build()
                                                    )
                                                    .collect(Collectors.toList());
        
        deletedFilesRepo.saveAll(deletedFiles);
        fileRepo.deleteAllById(fileIds);
        folderRepo.delete(folderMetaDataDb);
        return folderMetaDataDb;
    }
    
    @Transactional
    public ItemsListing deleteFileById(String fileId, String ownerId){
        FileMetaData fileMetaData = fileRepo.findByFileIdAndOwnerId(UUID.fromString(fileId), UUID.fromString(ownerId))
                                        .orElseThrow(()->new FileNotFoundException("File Not Found!"));
        if(!fileMetaData.getStatus().equals("S")) throw new FileNotFoundException("File Not Found!");
        FileDeleteTracker fileDeleteTracker = FileDeleteTracker.builder()
                                                .ownerId(fileMetaData.getOwnerId())
                                                .objectKey(fileMetaData.getObjectKey())
                                                .build();
        deletedFilesRepo.save(fileDeleteTracker);
        fileRepo.delete(fileMetaData);
        return ItemsListing.builder()
                    .itemId(fileMetaData.getFileId())
                    .ownerId(fileMetaData.getOwnerId())
                    .parentId(fileMetaData.getFolderId())
                    .createdAt(fileMetaData.getCreatedAt())
                    .updatedAt(fileMetaData.getUpdatedAt())
                    .fileSize(fileMetaData.getFileSize())
                    .itemName(fileMetaData.getFileName())
                    .mime(fileMetaData.getMime())
                    .build();
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
            .filter((file)->file.getStatus().equals("S"))
            .map((file)-> ItemsListing.builder()
                .itemId(file.getFileId())
                .itemName(file.getFileName())
                .objectKey(file.getObjectKey())
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
