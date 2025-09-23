package com.aravindweb.gatewayservice.utils;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    @Autowired
    AuthProperties authProperties;

    public Predicate<ServerHttpRequest> isSecured =
            request -> authProperties.getWhitelist()
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
