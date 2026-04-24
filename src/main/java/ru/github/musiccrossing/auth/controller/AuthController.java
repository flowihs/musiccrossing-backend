package ru.github.musiccrossing.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.service.AuthService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthResponse tokens = authService.register(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString()
        );

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString()
        );

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse tokens = authService.login(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());
        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @Valid @RequestBody GoogleAuthRequest request,
            HttpServletResponse response
    ) {
        AuthResponse tokens = authService.loginWithGoogle(request.getIdToken());

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());
        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/telegram")
    public ResponseEntity<AuthResponse> loginWithTelegram(
            @Valid @RequestBody TelegramLoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse tokens = authService.loginWithTelegram(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletResponse response
    ) {
        AuthResponse tokens = authService.refresh(request.getRefreshToken());

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request, HttpServletResponse response) {
        authService.logout(request);
        response.addHeader("Set-Cookie", logoutCookie("access_token"));
        response.addHeader("Set-Cookie", logoutCookie("refresh_token"));
    }

    @PostMapping("/logout-all")
    public void logoutAll(@RequestBody LogoutRequest request, HttpServletResponse response) {
        authService.logout(request);
        response.addHeader("Set-Cookie", logoutCookie("access_token"));
        response.addHeader("Set-Cookie", logoutCookie("refresh_token"));
    }

    private String logoutCookie(String nameCookie) {
        return ResponseCookie.from(nameCookie, "")
                .httpOnly(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .secure(false)
                .build()
                .toString();
    }

    private ResponseCookie createAccessCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .build();
    }
}
