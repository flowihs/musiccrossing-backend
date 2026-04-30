package ru.github.musiccrossing.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.dto.request.LoginRequest;
import ru.github.musiccrossing.auth.dto.request.RegisterRequest;
import ru.github.musiccrossing.auth.dto.request.UpdatePasswordRequest;
import ru.github.musiccrossing.auth.dto.response.AuthResponse;
import ru.github.musiccrossing.auth.entity.RecoverCompromisedAccountToken;
import ru.github.musiccrossing.auth.entity.RefreshToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.exception.auth.*;
import ru.github.musiccrossing.auth.exception.user.UserNotFoundException;
import ru.github.musiccrossing.auth.repository.RecoverCompromisedAccountTokenRepository;
import ru.github.musiccrossing.auth.repository.RefreshTokenRepository;


import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenVerifierService tokenVerifierService;
    private final RecoverCompromisedAccountTokenRepository recoverCompromisedAccountTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

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

        refreshTokenRepository.findById(refreshToken)
                .orElseThrow(InvalidTokenTypeException::new);
        refreshTokenRepository.deleteById(refreshToken);

        String newAccess = jwtService.generateAccessToken(userId);
        String newRefresh = jwtService.generateRefreshToken(userId);

        RefreshToken newEntity = RefreshToken.builder()
                .token(newRefresh)
                .userId(userId)
                .expiredAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .build();
        refreshTokenRepository.save(newEntity);

        return new AuthResponse(newAccess, newRefresh);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userService.login(request);

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refresh)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(access, refresh);
    }

    public AuthResponse register(RegisterRequest request) {
        var user = userService.register(request);

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refresh)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(access, refresh);
    }

    @Transactional
    public void recoverCompromisedAccount(String token, UpdatePasswordRequest request) {
        RecoverCompromisedAccountToken tokenEntity = recoverCompromisedAccountTokenRepository.findById(token)
                .orElseThrow(TokenNotFoundException::new);

        if (tokenEntity.getExpiredAt().before(new Date())) {
            throw new TokenExpiredException();
        }

        User user = userService.findById(tokenEntity.getUserId());

        recoverCompromisedAccountTokenRepository.delete(tokenEntity);

        userService.updatePassword(user.getId(), request, false, false);

        logoutUser(user.getId());
    }

    @SneakyThrows
    public AuthResponse loginWithGoogle(String idToken) {
        JWTClaimsSet claims = tokenVerifierService.verifyGoogle(idToken);

        String email = claims.getStringClaim("email");
        String googleId = claims.getSubject();
        String name = claims.getStringClaim("name");

        User user;
        try {
            user = userService.findByEmail(email);
        } catch (UserNotFoundException e) {
            user = userService.authenticateWithGoogle(email, googleId, name);
        }

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refresh)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(access, refresh);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Transactional
    public void logoutUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
