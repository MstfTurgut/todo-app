package com.mstftrgt.todoapp.service.jwt;

import com.mstftrgt.todoapp.configuration.JwtTokenConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final JwtTokenConfiguration jwtTokenConfiguration;

    public String generateToken(String username) {
        return Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenConfiguration.getExpirationTime()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtTokenConfiguration.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
