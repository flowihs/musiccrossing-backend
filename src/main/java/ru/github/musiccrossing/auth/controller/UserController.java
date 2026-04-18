package ru.github.musiccrossing.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.github.musiccrossing.auth.dto.ForgotPasswordRequest;
import ru.github.musiccrossing.auth.dto.ResetPasswordRequest;
import ru.github.musiccrossing.auth.service.UserService;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/forgot-password")
    public boolean forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public boolean resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(token, request);
    }

    @GetMapping("/is-existing-token")
    public boolean isExistingToken(@RequestParam String token) {
        return userService.isExistingToken(token);
    }
}
