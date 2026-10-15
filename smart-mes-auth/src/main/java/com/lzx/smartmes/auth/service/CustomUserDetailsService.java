package com.lzx.smartmes.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzx.smartmes.auth.entity.SysUser;
import com.lzx.smartmes.auth.mapper.SysUserMapper;
import com.lzx.smartmes.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper users;

    @Override
    public UserDetails loadUserByUsername(String username) {

        SysUser user = users.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new UsernameNotFoundException(
                    "user not found or inactive"
            );
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                users.permissions(user.getId())
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    public SysUser current(String username) {

        SysUser user = users.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        if (user == null) {
            throw new BusinessException(
                    "user not found"
            );
        }

        return user;
    }

    public List<String> permissions(Long id) {
        return users.permissions(id);
    }
}