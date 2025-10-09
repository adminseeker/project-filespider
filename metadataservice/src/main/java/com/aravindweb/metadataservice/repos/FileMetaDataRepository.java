package com.aravindweb.metadataservice.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aravindweb.metadataservice.entities.FileMetaData;
import java.util.List;
import java.util.Optional;


public interface FileMetaDataRepository extends JpaRepository<FileMetaData, UUID>{
    Optional<List<FileMetaData>> findByFolderIdIsNullAndOwnerId(UUID ownerId);
    Optional<List<FileMetaData>> findByFolderIdAndOwnerId(UUID folderId, UUID ownerId);
    Optional<FileMetaData> findByFileIdAndOwnerId(UUID fileId, UUID ownerId);
}
