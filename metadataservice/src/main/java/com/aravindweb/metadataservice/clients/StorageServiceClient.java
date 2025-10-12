package com.aravindweb.metadataservice.clients;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.aravindweb.metadataservice.clients.dto.StorageServiceRequest;
import com.aravindweb.metadataservice.clients.dto.StorageServiceResponse;


@FeignClient("storageservice")
public interface StorageServiceClient {
    @PostMapping("/api/v1/storage/privateapi/single/upload")
    Optional<StorageServiceResponse> getSingleUploadUrl(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/single/validate")
    Optional<StorageServiceResponse> dataValidation(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/multiupload/init")
    Optional<StorageServiceResponse> multiUploadInit(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/multiupload/sign")
    Optional<StorageServiceResponse> getMultiUploadPartsSignedUrls(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/multiupload/complete")
    Optional<StorageServiceResponse> completeMultiPartUpload(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/multiupload/abort")
    void abortMultiPartUpload(@RequestBody StorageServiceRequest request);

    @PostMapping("/api/v1/storage/privateapi/download")
    Optional<StorageServiceResponse> getDownloadUrl(@RequestBody StorageServiceRequest request);
}
