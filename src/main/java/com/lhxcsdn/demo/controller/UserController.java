package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.pojo.entity.User;
import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.service.UserService; // 引入 UserService
import com.lhxcsdn.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // ✨ 补上缺失的注册接口
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String msg = userService.register(user);
        if ("注册成功".equals(msg)) {
            return Result.success(msg);
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser == null) {
            // ✨ 修改点 2：根据你 Result 的定义，只传 String 消息
            return Result.error("用户不存在");
        }

        if (!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", dbUser.getId());
        claims.put("username", dbUser.getUsername());

        String token = JwtUtils.generateToken(claims);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", dbUser.getId());
        data.put("nickname", dbUser.getNickname());

        return Result.success(data);
    }

    /**
     * 获取个人资料
     */
    @GetMapping("/me")
    public Result getMe(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }

        try {
            String actualToken = token.substring(7);
            String username = JwtUtils.parseToken(actualToken).get("username", String.class);
            User user = userMapper.findByUsername(username);

            if (user != null) {
                user.setPassword(null);
            }
            return Result.success(user);

        } catch (Exception e) {
            return Result.error("凭证已失效");
        }
    }

    /**
     * 更新个人资料
     */
    @PostMapping("/update")
    public Result updateProfile(@RequestBody User user) {
        // ✨ 这里必须确保传进来的 user 是你 pojo.entity 里的 User
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        int rows = userMapper.updateUserInfo(user);

        if (rows > 0) {
            return Result.success("资料更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
}