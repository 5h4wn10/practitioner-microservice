package com.practitionerservice.config;

import com.practitionerservice.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET = "MY_SUPER_SECURE_SECRET_KEY_WITH_AT_LEAST_256_BITS"; // Minst 256 bits
    private final Key SECRET_KEY = new SecretKeySpec(
            Base64.getEncoder().encode(SECRET.getBytes()),
            SignatureAlgorithm.HS256.getJcaName()
    );

    // Validera token och returnera claims
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Generera JWT-token
    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", "ROLE_" + role.name()) // Lägg till ROLE_ prefix
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SECRET_KEY)
                .compact();
    }
}
