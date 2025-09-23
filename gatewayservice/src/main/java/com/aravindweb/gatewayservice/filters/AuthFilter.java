package com.aravindweb.gatewayservice.filters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.aravindweb.gatewayservice.dto.AuthResponse;
import com.aravindweb.gatewayservice.dto.TokenRequest;
import com.aravindweb.gatewayservice.exceptions.AccessDeniedException;
import com.aravindweb.gatewayservice.exceptions.AuthException;
import com.aravindweb.gatewayservice.utils.RouteValidator;

import reactor.core.publisher.Mono;

@Component
@Order(1)
public class AuthFilter implements GlobalFilter {
    
    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){

        if(exchange.getRequest().getPath().toString().toLowerCase().contains("privateapi")){
            throw new AccessDeniedException("Forbidden!");
        }

        if (routeValidator.isSecured.test(exchange.getRequest())){
            List<String> authHeaderList = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authHeaderList==null || !exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new AuthException("missing authorization header");
            }
            String authHeader = authHeaderList.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setToken(authHeader);
            return webClientBuilder
                .baseUrl("http://authservice")
                .build()
                .post()
                .uri("/api/v1/auth/validate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(tokenRequest),TokenRequest.class)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .map(authResponse -> {
                    ServerHttpRequest mutatedRequest =exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", String.valueOf(authResponse.getUserId()))
                            .build();
                    return exchange.mutate().request(mutatedRequest).build();
                })
                .onErrorResume(e -> {
                    return Mono.error(new AuthException("Invalid Token"));
                })
                .flatMap(chain::filter);
        }
        return chain.filter(exchange);
    };
}


