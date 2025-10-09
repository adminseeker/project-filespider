package com.aravindweb.authservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aravindweb.authservice.clients.dto.ErrorResponse;
import com.aravindweb.authservice.clients.dto.UserRequest;
import com.aravindweb.authservice.dto.AuthResponse;
import com.aravindweb.authservice.dto.LoginRequest;
import com.aravindweb.authservice.dto.Token;
import com.aravindweb.authservice.exceptions.AuthServiceException;
import com.aravindweb.authservice.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    AuthService authService;

    @PostMapping("/register")    
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest){
        try {
            ResponseEntity<Token> token = new ResponseEntity<Token>(authService.registerUser(userRequest),HttpStatus.OK);
            return token; 
        } catch (AuthServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        try {
            ResponseEntity<Token> token = new ResponseEntity<Token>(authService.loginUser(loginRequest),HttpStatus.OK);
            return token; 
        } catch (AuthServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Token token){
        try {
            ResponseEntity<AuthResponse> authResponse = new ResponseEntity<AuthResponse>(authService.validateToken(token),HttpStatus.OK);
            return authResponse; 
        } catch (AuthServiceException e) {
            ErrorResponse error = ErrorResponse.builder().errorMessage(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(error,e.getStatusCode());
        } catch (Exception e){
            ErrorResponse error = ErrorResponse.builder().errorMessage("Invalid Request!").build();
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);
        }
    }

}
