package ru.github.musiccrossing.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.dto.request.GoogleAuthRequest;
import ru.github.musiccrossing.auth.dto.request.LoginRequest;
import ru.github.musiccrossing.auth.dto.request.RefreshRequest;
import ru.github.musiccrossing.auth.dto.request.RegisterRequest;
import ru.github.musiccrossing.auth.dto.response.AuthResponse;
import ru.github.musiccrossing.auth.service.AuthService;
import ru.github.musiccrossing.auth.service.JwtService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody final RegisterRequest request, final HttpServletResponse response) {
        AuthResponse tokens = authService.register(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString()
        );

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString()
        );

        return tokens;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody final LoginRequest request, final HttpServletResponse response) {
        AuthResponse tokens = authService.login(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());
        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return tokens;
    }

    @PostMapping("/google")
    public AuthResponse loginWithGoogle(@RequestBody final GoogleAuthRequest request) {
        return authService.loginWithGoogle(request.getIdToken());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody final RefreshRequest request, final HttpServletResponse response) {
        AuthResponse tokens = authService.refresh(request.getRefreshToken());

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return tokens;
    }

    @PostMapping("/logout")
    public void logout(final HttpServletResponse response, HttpServletRequest request) {
        String refreshToken = jwtService.getRefreshTokenByCookies(request.getCookies());

        authService.logout(refreshToken);

        response.addHeader("Set-Cookie", logoutCookie("access_token"));
        response.addHeader("Set-Cookie", logoutCookie("refresh_token"));
    }

    @PostMapping("/logout-user")
    public void logoutUser(
            @RequestHeader("Authorization") final String authHeader,
            final HttpServletResponse response
    ) {
        Long userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        authService.logoutUser(userId);

        response.addHeader("Set-Cookie", logoutCookie("access_token"));
        response.addHeader("Set-Cookie", logoutCookie("refresh_token"));
    }

    private String logoutCookie(final String nameCookie) {
        return ResponseCookie.from(nameCookie, "")
                .httpOnly(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .secure(false)
                .build()
                .toString();
    }

    private ResponseCookie createAccessCookie(final String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshCookie(final String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .build();
    }
}
