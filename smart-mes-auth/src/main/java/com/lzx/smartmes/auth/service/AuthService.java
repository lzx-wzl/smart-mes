package com.lzx.smartmes.auth.service;

import com.lzx.smartmes.auth.config.JwtUtil;
import com.lzx.smartmes.auth.dto.LoginRequest;
import com.lzx.smartmes.auth.entity.SysUser;
import com.lzx.smartmes.auth.vo.LoginResponse;
import com.lzx.smartmes.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService users;

    private final PasswordEncoder encoder;

    private final JwtUtil jwt;

    public LoginResponse login(LoginRequest request) {

        SysUser user =
                users.current(
                        request.getUsername()
                );

        if (!encoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BusinessException(
                    "invalid username or password"
            );
        }

        return new LoginResponse(
                jwt.create(user.getUsername()),
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                users.permissions(user.getId())
        );
    }
}