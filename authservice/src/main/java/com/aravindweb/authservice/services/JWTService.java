package com.aravindweb.authservice.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aravindweb.authservice.dto.AuthResponse;
import com.aravindweb.authservice.exceptions.TokenExpiredException;
import com.aravindweb.authservice.exceptions.TokenValidationException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
public class JWTService {

    @Value("${JWT_SECRET}")
    public String SECRET;


    public AuthResponse validateToken(final String token) {
        AuthResponse response = new AuthResponse();
        try {
            if(isTokenExpired(token)) throw new TokenExpiredException("Token Expired!");
            response.setUserId(UUID.fromString(extractClaim(token,Claims::getSubject)));
            response.setEmail(extractClaim(token,claims -> claims.get("email",String.class)));
            return response;
        } catch (Exception e) {
            throw new TokenValidationException("Invalid Token!");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
      }

    private Claims extractAllClaims(String token) {
    return Jwts
        .parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

    public String generateToken(String userName, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email",email);
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

