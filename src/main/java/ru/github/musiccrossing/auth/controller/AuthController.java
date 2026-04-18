package ru.github.musiccrossing.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
    public AuthResponse register(@RequestBody RegisterRequest request, HttpServletResponse response) {
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
    public AuthResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse tokens = authService.login(request);

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());
        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return tokens;
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request, HttpServletResponse response) {
        AuthResponse tokens = authService.refresh(request.getRefreshToken());

        response.addHeader("Set-Cookie",
                createAccessCookie(tokens.getAccessToken()).toString());

        response.addHeader("Set-Cookie",
                createRefreshCookie(tokens.getRefreshToken()).toString());

        return tokens;
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
