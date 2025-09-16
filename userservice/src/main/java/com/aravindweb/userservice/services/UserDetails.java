package com.aravindweb.userservice.services;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aravindweb.userservice.dto.UserResponse;
import com.aravindweb.userservice.entities.User;
import com.aravindweb.userservice.exceptions.InvalidFieldException;
import com.aravindweb.userservice.exceptions.UserNotFoundException;
import com.aravindweb.userservice.exceptions.UserServiceException;
import com.aravindweb.userservice.repos.UserRepository;
import com.aravindweb.userservice.utils.UserValidation;

@Service
public class UserDetails {

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserValidation userValidation;

    public UserResponse addUser(User user) throws UserServiceException{
        userValidation.validateAdd(user);
        userRepo.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .country(user.getCountry())
                .build();
    }

    public UserResponse getUserDetailsById(String id) throws UserServiceException{
        User user = userRepo.findById(UUID.fromString(id)).orElseThrow(()-> new UserNotFoundException("User Not Found!"));
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .country(user.getCountry())
            .build();
    }

    public UserResponse getUserDetailsByEmail(User user) throws UserServiceException{
        if(!StringUtils.hasText(user.getEmail())) throw new InvalidFieldException("Invalid Email!");
        User userDb = userRepo.findByEmail(user.getEmail()).orElseThrow(()-> new UserNotFoundException("User Not Found!"));
        return UserResponse.builder()
            .id(userDb.getId())
            .firstName(userDb.getFirstName())
            .lastName(userDb.getLastName())
            .email(userDb.getEmail())
            .phone(userDb.getPhone())
            .country(userDb.getCountry())
            .build();
    }

    public UserResponse updateUserDetailsById(User user) throws UserServiceException{   
        User userDb = userValidation.validateUpdate(user);
        if(StringUtils.hasText(user.getFirstName())) userDb.setFirstName(user.getFirstName());
        if(StringUtils.hasText(user.getLastName())) userDb.setLastName(user.getLastName());
        if(StringUtils.hasText(user.getPassword())) userDb.setPassword(user.getPassword());
        if(StringUtils.hasText(user.getCountry())) userDb.setCountry(user.getCountry());
        if(StringUtils.hasText(user.getPhone())) userDb.setPhone(user.getPhone());
        userRepo.save(userDb);
        return UserResponse.builder()
            .id(userDb.getId())
            .firstName(userDb.getFirstName())
            .lastName(userDb.getLastName())
            .email(userDb.getEmail())
            .phone(userDb.getPhone())
            .country(userDb.getCountry())
            .build();
    }

    public UserResponse deleteUserById(User user) throws UserServiceException{   
        User userDb = userRepo.findById(user.getId()).orElseThrow(()-> new UserNotFoundException("User Not Found!"));
        userRepo.delete(userDb);
        return UserResponse.builder()
            .id(userDb.getId())
            .firstName(userDb.getFirstName())
            .lastName(userDb.getLastName())
            .email(userDb.getEmail())
            .phone(userDb.getPhone())
            .country(userDb.getCountry())
            .build();
    }
}
