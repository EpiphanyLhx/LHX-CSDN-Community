package com.lhxcsdn.demo.config;

import com.lhxcsdn.demo.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启 @PreAuthorize 注解支持，实现角色/权限校验
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 通过构造方法注入 JWT 过滤器
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（因为使用 JWT，不需要防止跨站请求伪造）
                .csrf(csrf -> csrf.disable())

                // 2. 禁用 Session（前后端分离，完全使用无状态的 JWT 模式）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 配置路径放行规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.ico", "/assets/**", "/static/**").permitAll()
                        // 放行登录和注册接口
                        .requestMatchers("/api/user/login", "/api/user/register","/error").permitAll()
                        .requestMatchers("/api/favorite/**").permitAll()

                        // 放行所有文章相关的查询接口（允许游客查看文章列表和详情） cxv
                        .requestMatchers(HttpMethod.GET, "/api/article/**").permitAll()
                        .requestMatchers("/api/user/me", "/api/user/update").permitAll()

                        // 其余所有请求（包括 /api/admin/**）必须经过身份认证
                        .anyRequest().authenticated()
                );

        // 4. ✨ 核心配置：将 JWT 过滤器放在用户名密码认证过滤器之前
        // 这样每次请求进来，会先校验 Token，校验通过后 Spring Security 就知道是谁在访问了
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}