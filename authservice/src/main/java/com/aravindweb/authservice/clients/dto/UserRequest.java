package com.aravindweb.authservice.clients.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private UUID id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
}
