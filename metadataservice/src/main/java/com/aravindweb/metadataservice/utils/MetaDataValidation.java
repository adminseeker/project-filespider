package com.aravindweb.metadataservice.utils;


import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aravindweb.metadataservice.entities.FolderMetaData;
import com.aravindweb.metadataservice.exceptions.UserNotFoundException;


@Component
public class MetaDataValidation {

    public void validateCreateFolder(FolderMetaData folderMetaData, HttpHeaders headers){
        String userId = validateXUserId(headers);
        folderMetaData.setOwnerId(UUID.fromString(userId));
        if(folderMetaData.getFolderId()!=null) {folderMetaData.setFolderId(null);}
    }


    public String validateXUserId(HttpHeaders headers){
        String userId = headers.getFirst("X-User-Id");
        if(!StringUtils.hasText(userId)) throw new UserNotFoundException("User Id Not Found!");
        return userId;
    }

    // public void validateAuth(HttpHeaders headers, User user){
    //     validateXUserId(headers);
    //     if(user==null || user.getId()==null || !user.getId().toString().equals(headers.getFirst("X-User-Id"))){
    //         throw new AuthException("Unauthorized Access!");
    //     }
    // }
}
