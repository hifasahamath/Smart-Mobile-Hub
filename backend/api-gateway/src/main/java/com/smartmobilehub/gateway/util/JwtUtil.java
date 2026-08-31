package com.smartmobilehub.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Utility for JWT token validation at the gateway layer.
 * The gateway does NOT generate tokens — only validates and extracts claims.
 * Token generation is the auth-service's responsibility.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Validates the JWT signature and expiration.
     * Returns the parsed claims if valid, throws JwtException otherwise.
     */
    public Claims validateAndExtractClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Quick validation check — returns true if token is valid.
     */
    public boolean isValid(String token) {
        try {
            validateAndExtractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
