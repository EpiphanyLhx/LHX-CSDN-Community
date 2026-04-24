package com.lhxcsdn.demo.filter;

import com.lhxcsdn.demo.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token 失效或伪造
            System.out.println("Token 验证失败");
        }

        filterChain.doFilter(request, response);
    }
}