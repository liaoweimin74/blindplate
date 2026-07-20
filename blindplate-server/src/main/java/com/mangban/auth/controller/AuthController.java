package com.mangban.auth.controller;

import com.mangban.auth.dto.LoginRequest;
import com.mangban.auth.dto.LoginResponse;
import com.mangban.auth.service.AuthService;
import com.mangban.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
