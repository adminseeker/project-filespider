CREATE TABLE t_folder_metadata (
  folder_id BINARY(16) NOT NULL COMMENT 'PK',
  folder_name VARCHAR(768) NOT NULL COMMENT 'Original folder name',
  owner_id BINARY(16) NOT NULL COMMENT 'Owner identifier (user id, etc.)',
  parent_folder BINARY(16) DEFAULT NULL COMMENT 'parent folder',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (folder_id),
  KEY idx_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Metadata table for uploaded folders';

CREATE TABLE t_file_metadata (
  file_id BINARY(16) NOT NULL COMMENT 'PK',
  file_name VARCHAR(1024) NOT NULL COMMENT 'Original file name',
  mime VARCHAR(255) NOT NULL COMMENT 'MIME type (e.g. application/pdf)',
  file_size BIGINT UNSIGNED NOT NULL COMMENT 'Size in bytes',
  checksum VARCHAR(128) NOT NULL COMMENT 'Checksum (MD5/SHA256, etc.)',
  status VARCHAR(32) NOT NULL DEFAULT 'P' COMMENT 'P - pending, S - Success, U - uploading, F - failed',
  object_key VARCHAR(768) NOT NULL COMMENT 'Physical/object storage path or URL',
  owner_id BINARY(16) NOT NULL COMMENT 'Owner identifier (user id, etc.)',
  folder_id BINARY(16) DEFAULT NULL COMMENT 'Parent Folder Id',
  version VARCHAR(64) DEFAULT NULL COMMENT 'Version tag or number',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  expires_at DATETIME(6) DEFAULT NULL COMMENT 'Optional expiry/TTL for the object',
  PRIMARY KEY (file_id),
  KEY idx_owner (owner_id),
  KEY idx_status (status),
  KEY idx_object_key (object_key(255)),
  KEY idx_folder_id (folder_id),
  CONSTRAINT fk_file_folder FOREIGN KEY (folder_id)
    REFERENCES t_folder_metadata (folder_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Metadata table for uploaded files';


