package com.aravindweb.metadataservice.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="t_folder_metadata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderMetaData {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)",name = "folder_id",updatable = false)
    private UUID folderId;

    @Column(name = "folder_name", length = 768, nullable = false)
    private String folderName;

    @Column(name = "owner_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID ownerId;

    @Column(name = "parent_folder", columnDefinition = "BINARY(16)")
    private UUID parentFolder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
