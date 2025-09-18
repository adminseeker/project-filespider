package com.aravindweb.authservice.clients;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.aravindweb.authservice.clients.dto.UserRequest;
import com.aravindweb.authservice.clients.dto.UserResponse;

@FeignClient("userservice")
public interface UserServiceClient {
    @PostMapping("/api/v1/users")
    Optional<UserResponse> registerUser(@RequestBody UserRequest user);

    @GetMapping("/api/v1/users/private/{id}")
    Optional<UserResponse> getUserDetails(@PathVariable String id);
}
