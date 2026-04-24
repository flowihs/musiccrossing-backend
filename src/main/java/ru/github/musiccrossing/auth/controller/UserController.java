package ru.github.musiccrossing.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.dto.*;
import ru.github.musiccrossing.auth.service.UserService;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
}
