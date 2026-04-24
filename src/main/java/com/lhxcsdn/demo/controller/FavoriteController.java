package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.mapper.FavoriteMapper;
import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Favorite;
import com.lhxcsdn.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    // 解析 Token 获取用户 ID 的复用方法
    private Integer getUserIdByToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) return null;
        try {
            return JwtUtils.parseToken(token.substring(7)).get("id", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    // 1. 切换收藏状态（点一下收藏，再点取消）
    @PostMapping("/toggle")
    public Result toggleFavorite(@RequestParam Integer articleId, @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = getUserIdByToken(token);
        if (userId == null) return Result.error("请先登录后再操作");

        Favorite fav = favoriteMapper.checkFavorite(userId, articleId);
        if (fav != null) {
            favoriteMapper.removeFavorite(userId, articleId);
            return Result.success("已取消收藏");
        } else {
            favoriteMapper.addFavorite(userId, articleId);
            return Result.success("收藏成功");
        }
    }

    // 2. 个人中心：获取我的收藏列表
    @GetMapping("/my")
    public Result getMyCollections(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = getUserIdByToken(token);
        if (userId == null) return Result.error("会话已过期，请重新登录");

        List<Article> list = favoriteMapper.findMyCollections(userId);
        return Result.success(list);
    }
}