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
@Table(name="t_deleted_file_metadata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDeleteTracker {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)",name = "delete_id",updatable = false)
    private UUID deleteId;

    @Column(name = "object_key", length = 768, nullable = false)
    private String objectKey;

    @Column(name = "owner_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID ownerId;

    @Column(name = "purged")
    private boolean purged;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
