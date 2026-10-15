package com.lzx.smartmes.auth.controller;

import com.lzx.smartmes.auth.dto.LoginRequest;
import com.lzx.smartmes.auth.service.AuthService;
import com.lzx.smartmes.auth.vo.LoginResponse;
import com.lzx.smartmes.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/login")
    @Operation(summary = "登录并获取 JWT")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return Result.ok(
                auth.login(request)
        );
    }
}