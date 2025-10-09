package com.aravindweb.metadataservice.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aravindweb.metadataservice.entities.FolderMetaData;
import java.util.List;
import java.util.Optional;


public interface FolderMetaDataRepository extends JpaRepository<FolderMetaData, UUID>{
    Optional<List<FolderMetaData>> findByOwnerId(UUID ownerId);
    Optional<FolderMetaData> findByFolderIdAndOwnerId(UUID folderId, UUID ownerId);
    Optional<List<FolderMetaData>> findByParentFolderAndOwnerId(UUID parentFolder, UUID ownerId);
    Optional<List<FolderMetaData>> findByParentFolderIsNullAndOwnerId(UUID ownerId);
}
