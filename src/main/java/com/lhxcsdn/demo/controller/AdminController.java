package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // 1. 获取所有用户列表 (要求必须是 SUPER_ADMIN 或者 USER_ADMIN 角色才能访问)
    // 实际项目中你需要注入 UserService 并查询数据库，这里用 Mock 数据演示结构
    @GetMapping("/users")
    //@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'USER_ADMIN')") // 👈 鉴权拦截
    public Result getAllUsers() {
        // 模拟数据库返回的用户数据（包含他们的角色）
        List<Map<String, Object>> users = new ArrayList<>();

        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1); admin.put("username", "admin"); admin.put("nickname", "超级管理员"); admin.put("role", "SUPER_ADMIN");

        Map<String, Object> testUser = new HashMap<>();
        testUser.put("id", 2); testUser.put("username", "zhangsan"); testUser.put("nickname", "张三"); testUser.put("role", "CONTENT_ADMIN");

        users.add(admin); users.add(testUser);
        return Result.success(users);
    }

    // 2. 给用户分配角色
    @PostMapping("/assignRole")
    //@PreAuthorize("hasAuthority('SUPER_ADMIN')") // 只有最高管理员能分配角色
    public Result assignRole(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String roleCode = params.get("roleCode").toString();

        return Result.success("角色分配成功！");
    }

    // 3. 测试权限：删除用户
    @DeleteMapping("/user/{id}")
    //@PreAuthorize("hasAuthority('sys:user:delete')") // 👈 必须具有删除用户权限才能请求
    public Result deleteUser(@PathVariable Long id) {
        return Result.success("删除用户成功");
    }
}