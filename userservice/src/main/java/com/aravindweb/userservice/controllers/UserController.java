package com.aravindweb.userservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.userservice.dto.ErrorResponse;
import com.aravindweb.userservice.dto.UserResponse;
import com.aravindweb.userservice.dto.UserResponseWithPassword;
import com.aravindweb.userservice.entities.User;
import com.aravindweb.userservice.exceptions.UserServiceException;
import com.aravindweb.userservice.services.UserDetailsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    UserDetailsService userDetails;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        try {
            ResponseEntity<UserResponse> user = new ResponseEntity<UserResponse>(userDetails.getUserDetailsById(id),HttpStatus.OK);
            return user;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/private/{id}")
    public ResponseEntity<?> getUserWithPasswordById(@PathVariable String id){
        try {
            ResponseEntity<UserResponseWithPassword> user = new ResponseEntity<UserResponseWithPassword>(userDetails.getUserDetailsWithPasswordById(id),HttpStatus.OK);
            return user;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> getUserByEmail(@RequestBody User user){
        try {
            ResponseEntity<UserResponse> userResp = new ResponseEntity<UserResponse>(userDetails.getUserDetailsByEmail(user),HttpStatus.OK);
            return userResp;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }
    
   @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody User user){
        try {
            ResponseEntity<UserResponse> newUser = new ResponseEntity<UserResponse>(userDetails.addUser(user),HttpStatus.CREATED);
            return newUser;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        try {
            ResponseEntity<UserResponse> updatedUser = new ResponseEntity<UserResponse>(userDetails.updateUserDetailsById(user),HttpStatus.OK);
            return updatedUser;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> DeleteUser(@RequestBody User user){
        try {
            ResponseEntity<UserResponse> deletedUser = new ResponseEntity<UserResponse>(userDetails.deleteUserById(user),HttpStatus.OK);
            return deletedUser;
        } catch (UserServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

}
