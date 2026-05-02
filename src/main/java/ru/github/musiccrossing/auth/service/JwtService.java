package ru.github.musiccrossing.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.auth.exception.auth.CookieEmptyException;
import ru.github.musiccrossing.auth.exception.auth.InvalidTokenException;
import ru.github.musiccrossing.auth.exception.auth.TokenNotFoundException;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(final Long userId) {
        return buildToken(userId, "access", accessExpiration);
    }

    public String generateRefreshToken(final Long userId) {
        return buildToken(userId, "refresh", refreshExpiration);
    }

    public String getRefreshTokenByCookies(final Cookie[] cookies) {
        if (cookies == null) {
            throw new CookieEmptyException();
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new TokenNotFoundException();
        }

        return refreshToken;
    }

    public String getAccessTokenByCookies(final Cookie[] cookies) {
        if (cookies == null) {
            throw new TokenNotFoundException();
        }

        String accessToken = null;
        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                accessToken = cookie.getValue();
                break;
            }
        }

        if (accessToken == null) {
            throw new TokenNotFoundException();
        }

        return accessToken;
    }

    public Long extractUserId(final String token) {
        return extractClaim(token, claims -> Long.parseLong(claims.getSubject()));
    }

    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(final String token, final Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public boolean validateToken(final String token, final Long userId) {
        try {
            Claims claims = extractAllClaims(token);
            Long tokenUserId = Long.parseLong(claims.getSubject());
            return tokenUserId.equals(userId) && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserIdFromAuthHeader(final String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }

        String token = authHeader.substring(7);
        return extractUserId(token);
    }

    public String extractTokenType(final String token) {
        return extractClaim(token, claims -> claims.get("token_type", String.class));
    }

    private String buildToken(final Long userId, final String type, final long expirationMs) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("token_type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(final Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
