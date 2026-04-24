package com.lhxcsdn.demo.service.impl;

import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.pojo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 去数据库找人
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("账号不存在！");
        }

        // 2. 收集这个人的所有角色和权限
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = userMapper.findRoleCodesByUserId(user.getId());
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role)); // 塞入角色
        }

        List<String> permissions = userMapper.findPermissionCodesByUserId(user.getId());
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission)); // 塞入权限
        }

        // 3. 交给 Spring Security
        // 注意：{noop} 表示不对密码进行加密比对（仅限我们本地开发测试用）
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                "{noop}" + user.getPassword(),
                authorities
        );
    }
}