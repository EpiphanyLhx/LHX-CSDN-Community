package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.mapper.RoleMapper;
import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.pojo.entity.Role;
import com.lhxcsdn.demo.pojo.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public AdminController(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    // 1. 获取所有用户列表 (要求必须是 SUPER_ADMIN 或者 USER_ADMIN 角色才能访问)
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'USER_ADMIN', 'sys:user:view')")
    public Result getAllUsers() {
        List<User> users = userMapper.findAllWithRole();
        // 确保每个用户都有角色（如果没有分配角色，则设置为 USER）
        for (User user : users) {
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
                user.setRoleName("普通用户");
            }
        }
        return Result.success(users);
    }

    // 2. 给用户分配角色（需要 sys:role:assign 权限或 SUPER_ADMIN 角色）
    @PostMapping("/assignRole")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'sys:role:assign')")
    public Result assignRole(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String roleCode = params.get("roleCode").toString();
        
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 检查角色是否存在
        Role role = roleMapper.findByCode(roleCode);
        if (role == null) {
            return Result.error("角色不存在");
        }
        
        // 检查是否为超级管理员账号（禁止修改admin的角色）
        if ("admin".equals(user.getUsername()) && !"SUPER_ADMIN".equals(roleCode)) {
            return Result.error("禁止修改超级管理员的角色");
        }
        
        // 更新用户角色（先删除原有角色，再添加新角色）
        // 首先检查用户是否已有角色
        int hasRole = userMapper.hasRole(userId, roleCode);
        if (hasRole > 0) {
            return Result.success("用户已拥有该角色");
        }
        
        // 删除用户所有现有角色（简化：每个用户只分配一个角色）
        // 实际中可能允许多个角色，这里简化处理
        List<String> existingRoles = userMapper.findRoleCodesByUserId(userId);
        for (String existingRoleCode : existingRoles) {
            Role existingRole = roleMapper.findByCode(existingRoleCode);
            if (existingRole != null) {
                roleMapper.removeRoleFromUser(userId, existingRole.getId());
            }
        }
        
        // 分配新角色
        int result = roleMapper.assignRoleToUser(userId, role.getId());
        if (result > 0) {
            return Result.success("角色分配成功！");
        } else {
            return Result.error("角色分配失败");
        }
    }

    // 3. 删除用户（需要 sys:user:delete 权限）
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result deleteUser(@PathVariable Long id) {
        // 检查用户是否存在
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 禁止删除超级管理员账号
        if ("admin".equals(user.getUsername())) {
            return Result.error("禁止删除超级管理员账号");
        }
        
        int result = userMapper.deleteById(id);
        if (result > 0) {
            return Result.success("删除用户成功");
        } else {
            return Result.error("删除用户失败");
        }
    }
    
    // 4. 获取所有角色列表（用于前端下拉选择）
    @GetMapping("/roles")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'USER_ADMIN', 'sys:role:assign')")
    public Result getAllRoles() {
        List<Role> roles = roleMapper.findAll();
        return Result.success(roles);
    }

    // 5. 清理系统日志（需要 sys:log:clean 权限）
    @PostMapping("/logs/clean")
    @PreAuthorize("hasAuthority('sys:log:clean')")
    public Result cleanLogs() {
        // 模拟日志清理操作（实际项目中会删除日志文件或清理数据库日志表）
        // 这里返回成功消息
        return Result.success("系统日志清理完成，共清理 0 条日志（模拟）");
    }
    
    // 6. 调试接口：检查当前用户权限（仅开发测试用）
    @GetMapping("/debug/permissions")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'sys:user:view')")
    public Result debugPermissions(@AuthenticationPrincipal Object principal) {
        Map<String, Object> result = new HashMap<>();
        
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) principal;
            result.put("username", userDetails.getUsername());
            List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            result.put("authorities", authorities);
        } else if (principal instanceof String) {
            result.put("username", principal.toString());
            result.put("authorities", "无法获取权限信息（可能是JWT过滤器设置的Authentication）");
        }
        
        // 获取当前用户ID（从SecurityContext）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) {
                result.put("userId", user.getId());
                List<String> roleCodes = userMapper.findRoleCodesByUserId(user.getId());
                List<String> permissionCodes = userMapper.findPermissionCodesByUserId(user.getId());
                result.put("dbRoles", roleCodes);
                result.put("dbPermissions", permissionCodes);
            }
        }
        
        return Result.success(result);
    }
}