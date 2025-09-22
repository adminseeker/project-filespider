package com.aravindweb.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aravindweb.authservice.clients.UserServiceClient;
import com.aravindweb.authservice.clients.dto.UserRequest;
import com.aravindweb.authservice.clients.dto.UserResponse;
import com.aravindweb.authservice.config.CustomUserDetails;
import com.aravindweb.authservice.dto.AuthResponse;
import com.aravindweb.authservice.dto.LoginRequest;
import com.aravindweb.authservice.dto.Token;
import com.aravindweb.authservice.exceptions.InvalidFieldException;
import com.aravindweb.authservice.exceptions.LoginException;

@Service    
public class AuthService {

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    JWTService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Token registerUser(UserRequest user){
        Token token = new Token();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserResponse newUser = userServiceClient.registerUser(user).orElseThrow(()->new InvalidFieldException("User Registration Failed!"));
        token.setToken(jwtService.generateToken(newUser.getId().toString(), newUser.getEmail()));
        return token;
    }

    public Token loginUser(LoginRequest loginRequest){
        return generateToken(loginRequest);
    }

    public AuthResponse validateToken(Token token){
        return jwtService.validateToken(token.getToken());
    }

    private Token generateToken(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            Token token = new Token();
            CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
            token.setToken(jwtService.generateToken(customUserDetails.getUsername(), loginRequest.getEmail()));
            return token;
        } else {
            throw new LoginException("Login Error!");
        }
           
    }

}
