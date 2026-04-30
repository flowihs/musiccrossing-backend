package ru.github.musiccrossing.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.dto.request.*;
import ru.github.musiccrossing.auth.dto.response.UpdateAccountDataResponse;
import ru.github.musiccrossing.auth.dto.response.UserResponse;
import ru.github.musiccrossing.auth.service.AuthService;
import ru.github.musiccrossing.auth.service.JwtService;
import ru.github.musiccrossing.auth.service.UserService;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/forgot-password")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public boolean resetPassword(
            @RequestParam String token,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        return userService.resetPassword(token, request);
    }

    @GetMapping("/forgot-password/is-existing-token")
    public boolean isExistingToken(@RequestParam String token) {
        return userService.isExistingToken(token);
    }

    @PostMapping("/generate-confirm-email-token")
    public void generateConfirmEmailToken(@Valid @RequestBody GenerateEmailConfirmTokenRequest request) {
        userService.generateConfirmEmailToken(request);
    }

    @PostMapping("/confirm-email")
    public boolean confirmEmail(@RequestParam String token) {
        return userService.confirmEmailByToken(token);
    }

    @PostMapping("/update-data")
    public ResponseEntity<UpdateAccountDataResponse> updateAccountData(
            @Valid @RequestBody UpdateAccountDataRequest request) {
        return ResponseEntity.ok(userService.updateAccountData(request));
    }

    @PostMapping("/recover-compromised-account")
    public void recoverCompromisedAccount(@RequestParam String token, @RequestBody UpdatePasswordRequest request) {
        authService.recoverCompromisedAccount(token, request);
    }

    @PostMapping("/update-password")
    public void updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        Long userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        userService.updatePassword(userId, request, true, true);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<UserResponse> getMyProfile(HttpServletRequest request) {
        Cookie[] cookie = request.getCookies();
        String accessToken = jwtService.getAccessTokenByCookies(cookie);
        Long userId = jwtService.extractUserId(accessToken);
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }
}
