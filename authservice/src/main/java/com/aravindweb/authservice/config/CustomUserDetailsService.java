package com.aravindweb.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;

import com.aravindweb.authservice.clients.UserServiceClient;
import com.aravindweb.authservice.clients.dto.UserRequest;
import com.aravindweb.authservice.clients.dto.UserResponse;

import java.util.Optional;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        Optional<UserResponse> userResponse = userServiceClient.getUserDetails(userRequest);
        return userResponse.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found with email: " + email));
    }

}

