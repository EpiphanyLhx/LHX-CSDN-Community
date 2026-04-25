package com.lhxcsdn.demo.filter;

import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserMapper userMapper;

    public JwtAuthenticationFilter(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头里拿到 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 如果没有带 Token，直接放行（交给后面的 Security 规则拦截）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 解析 Token
        String token = authHeader.substring(7);
        try {
            Claims claims = JwtUtils.parseToken(token);
            String username = claims.get("username", String.class);

            // 4. 将用户信息存入 Spring Security 上下文（证明已登录）
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 获取用户ID
                Long userId = null;
                try {
                    userId = claims.get("id", Long.class);
                } catch (Exception e) {
                    // 如果无法获取Long类型的id，尝试Integer
                    Integer idInt = claims.get("id", Integer.class);
                    if (idInt != null) {
                        userId = idInt.longValue();
                    }
                }
                // 如果userId仍然为null，通过用户名查询用户
                if (userId == null) {
                    com.lhxcsdn.demo.pojo.entity.User user = userMapper.findByUsername(username);
                    if (user != null) {
                        userId = user.getId();
                    }
                }
                // 加载用户权限
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (userId != null) {
                    // 获取用户角色
                    List<String> roleCodes = userMapper.findRoleCodesByUserId(userId);
                    // 获取用户权限
                    List<String> permissionCodes = userMapper.findPermissionCodesByUserId(userId);
                    // 将角色和权限都作为 GrantedAuthority 添加
                    for (String roleCode : roleCodes) {
                        authorities.add(new SimpleGrantedAuthority(roleCode));
                    }
                    for (String permissionCode : permissionCodes) {
                        authorities.add(new SimpleGrantedAuthority(permissionCode));
                    }
                    // 调试日志
                    System.out.println("用户 " + username + " 角色: " + roleCodes + " 权限: " + permissionCodes);
                } else {
                    System.out.println("用户 " + username + " 未找到ID");
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token 失效或伪造
            System.out.println("Token 验证失败: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}