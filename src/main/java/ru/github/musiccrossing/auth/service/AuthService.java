package ru.github.musiccrossing.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.entity.RefreshToken;
import ru.github.musiccrossing.auth.entity.User;
import ru.github.musiccrossing.auth.exception.*;
import ru.github.musiccrossing.auth.repository.RefreshTokenRepository;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenVerifierService tokenVerifierService;

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

        RefreshToken oldToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(InvalidTokenTypeException::new);

        if (oldToken.getExpiredAt().before(new Date())) {
            throw new RefreshTokenExpiredException();
        }

        refreshTokenRepository.delete(oldToken);

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

    @SneakyThrows
    @Transactional
    public AuthResponse loginWithGoogle(String idToken) {
        JWTClaimsSet claims = tokenVerifierService.verifyGoogle(idToken);

        String email = claims.getStringClaim("email");
        String googleId = claims.getSubject();
        String name = claims.getStringClaim("name");

        User user;
        try {
            user = userService.findByEmail(email);

            if (user.getGoogleId() == null) {
                userService.linkedAccountsWithGoogle(user, googleId);
            }
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

    public AuthResponse loginWithTelegram(TelegramLoginRequest request) {
        Map<String, String> telegramData = new HashMap<>();

        telegramData.put("id", request.getId());
        telegramData.put("username", request.getUsername());
        telegramData.put("first_name", request.getFirst_name());
        telegramData.put("auth_date", request.getAuth_date());
        telegramData.put("hash", request.getHash());

        Map<String, String> verifiedData = tokenVerifierService.verifyTelegram(telegramData);

        UserResponse user = userService.findOrCreateTelegramUser(verifiedData);

        String access = jwtService.generateAccessToken(user.getId());
        String refresh = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refresh)
                .userId(user.getId())
                .expiredAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return  new AuthResponse(access, refresh);
    }

    public void logout(LogoutRequest request) {
        Long userId = jwtService.extractUserId(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findById(request.getRefreshToken())
                .orElseThrow(TokenNotFoundException::new);

        if (!token.getUserId().equals(userId)) {
            throw new AuthException("Токен не принадлежит пользователю", HttpStatus.BAD_REQUEST);
        }

        refreshTokenRepository.deleteById(request.getRefreshToken());
    }

    public void logoutAll(LogoutRequest request) {
        Long userId = jwtService.extractUserId(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findById(request.getRefreshToken())
                .orElseThrow(TokenNotFoundException::new);

        if (!token.getUserId().equals(userId)) {
            throw new AuthException("Токен не принадлежит пользователю", HttpStatus.BAD_REQUEST);
        }

        refreshTokenRepository.deleteByUserId(userId);
    }
}
