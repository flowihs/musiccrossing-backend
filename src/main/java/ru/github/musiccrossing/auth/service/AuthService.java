package ru.github.musiccrossing.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.auth.dto.AuthResponse;
import ru.github.musiccrossing.auth.dto.LoginRequest;
import ru.github.musiccrossing.auth.dto.RegisterRequest;
import ru.github.musiccrossing.auth.exception.InvalidTokenTypeException;
import ru.github.musiccrossing.auth.exception.RefreshTokenExpiredException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthResponse refresh(String refreshToken) {
        Date expiration = jwtService.extractExpiration(refreshToken);

        if (expiration.before(new Date())) {
            throw new RefreshTokenExpiredException();
        }

        String type = jwtService.extractTokenType(refreshToken);

        if (!"refresh".equals(type)) {
            throw new InvalidTokenTypeException();
        }

        Long userId = jwtService.extractUserId(refreshToken);

        String newAccess = jwtService.generateAccessToken(userId);
        String newRefresh = jwtService.generateRefreshToken(userId);

        return new AuthResponse(newAccess, newRefresh);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userService.login(request);

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse register(RegisterRequest request) {
        var user = userService.register(request);

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(access, refresh);
    }
}
