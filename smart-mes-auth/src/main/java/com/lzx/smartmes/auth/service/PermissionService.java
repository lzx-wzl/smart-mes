package com.lzx.smartmes.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final CustomUserDetailsService users;

    public boolean hasPermission(Long userId, String permission) {

        return users.permissions(userId).contains(permission);
    }
}