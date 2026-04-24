package com.lhxcsdn.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // ✨ 核心修复：使用一段足够长的固定字符串作为秘钥（至少需要 32 个字符）
    private static final String SECRET_STRING = "LhxcsdnDemoCommunity2026SuperSecretKeyMustBeLongEnough";

    // 用固定的字符串生成不变的 KEY
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Token 有效期设为 12 小时
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 12;

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}