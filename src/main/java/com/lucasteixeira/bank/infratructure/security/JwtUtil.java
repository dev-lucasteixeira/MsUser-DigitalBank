package com.lucasteixeira.bank.infratructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    @Value("${api.security.token.secret}")
    private String secretKey;

    private SecretKey getSecretKey(){
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmailToken(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username){
        final String extractedUsername = extractEmailToken(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }
}
