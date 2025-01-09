package com.example.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class JwtProvider {
    private SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth){
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        String jwt = Jwts.builder().setIssuedAt(new Date())
                .setExpiration((new Date(new Date().getTime() + 86400000))) //24 hours
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();
        return jwt;
    }

    public String getEmailFromJwtToken(String jwt) {
        try {
            if (jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7); // Remove 'Bearer ' prefix
            }

            // Parse the JWT and get claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // Make sure the signing key is consistent
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            // Log claims for debugging
            log.info("JWT Claims: {}", claims);

            // Extract the email claim
            String email = claims.get("email", String.class);

            // Return the extracted email
            return email;
        } catch (JwtException e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid or expired token");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Error reading JWT", e);
        }
    }


    private String populateAuthorities(Collection <? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();

        for(GrantedAuthority authority:authorities){
            auths.add(authority.getAuthority());
        }

        return String.join(",", auths);
    }
}
