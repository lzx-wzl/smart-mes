package com.lzx.smartmes.auth.vo;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;

    private String realName;

    private List<String> permissions;
}