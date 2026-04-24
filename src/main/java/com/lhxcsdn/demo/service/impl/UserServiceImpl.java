package com.lhxcsdn.demo.service.impl;

import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.pojo.entity.User;
import com.lhxcsdn.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @Override
    public String register(User user) {
        // 1. 检查用户名是否存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return "用户名已存在";
        }
        // 2. 执行插入（实际项目这里要给密码加密，我们先跑通逻辑）
        userMapper.insert(user);
        return "注册成功";
    }

}
