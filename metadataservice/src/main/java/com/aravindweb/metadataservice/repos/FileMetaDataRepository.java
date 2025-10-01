package com.aravindweb.metadataservice.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aravindweb.metadataservice.entities.FileMetaData;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, UUID>{
    
}
