package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Favorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    // 1. 查询是否已收藏
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND article_id = #{articleId}")
    Favorite checkFavorite(@Param("userId") Integer userId, @Param("articleId") Integer articleId);

    // 2. 添加收藏
    @Insert("INSERT INTO favorite(user_id, article_id) VALUES(#{userId}, #{articleId})")
    int addFavorite(@Param("userId") Integer userId, @Param("articleId") Integer articleId);

    // 3. 取消收藏
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND article_id = #{articleId}")
    int removeFavorite(@Param("userId") Integer userId, @Param("articleId") Integer articleId);

    // 4. 连表查询：获取当前用户收藏的所有文章（按收藏时间倒序）
    @Select("SELECT a.* FROM article a INNER JOIN favorite f ON a.id = f.article_id WHERE f.user_id = #{userId} ORDER BY f.create_time DESC")
    List<Article> findMyCollections(Integer userId);
}