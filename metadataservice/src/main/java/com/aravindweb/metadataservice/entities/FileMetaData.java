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
@Table(name="t_file_metadata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaData {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)",name = "file_id",updatable = false)
    private UUID fileId;

    @Column(name = "file_name", length = 1024, nullable = false)
    private String fileName;

    @Column(name = "mime", length = 255, nullable = false)
    private String mime;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "checksum", length = 128, nullable = false)
    private String checksum;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "object_key", length = 768, nullable = false)
    private String objectKey;

    @Column(name = "owner_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID ownerId;

    @Column(name = "folder_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID folder_id;

    @Column(name = "version", length = 64)
    private String version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
