package com.lhxcsdn.demo.service;

import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.pojo.entity.Tag;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    // 获取所有文章列表
    List<Article> getAllArticles();

    // 根据 ID 获取文章详情
    Article getById(Long id);

    // 获取文章的所有评论
    List<Comment> getCommentsByArticleId(Long articleId);

    // 保存评论
    void saveComment(Comment comment);

    // 增加点赞数
    void addLike(Long id);

    // 发表文章
    void publishArticle(Article article);

    //个人中心
    List<Article> getArticlesByAuthor(Long authorId);

    List<Article> search(String keyword);

    // 获取热门文章排行榜
    List<Article> getHotArticles();

    // 删除文章（需要 sys:article:delete 权限）
    void deleteArticle(Long id);

    // 删除评论（需要 sys:comment:delete 权限）
    void deleteComment(Long commentId);

    // ========== 管理员功能 ==========

    // 管理员文章列表（带筛选）
    List<Article> getArticlesForAdmin(String status, String category, String keyword);

    // 更新文章状态
    void updateArticleStatus(Long id, String status);

    // 管理员评论列表（带筛选）
    List<Comment> getCommentsForAdmin(String status, Long articleId, String keyword);

    // 更新评论审核状态
    void updateCommentStatus(Long id, String status, String auditReason, Long auditBy);

    // 批量更新评论状态
    void batchUpdateCommentStatus(List<Long> ids, String status, String auditReason, Long auditBy);

    // 获取文章统计
    List<Map<String, Object>> getArticleStats();

    // 获取评论统计
    List<Map<String, Object>> getCommentStats();

    // 更新文章（编辑）
    void updateArticle(Article article);

    // ========== 标签管理功能 ==========

    // 查询所有标签
    List<Tag> getAllTags();

    // 根据ID查询标签
    Tag getTagById(Long id);

    // 根据名称查询标签
    Tag getTagByName(String name);

    // 创建标签
    void createTag(Tag tag);

    // 更新标签
    void updateTag(Tag tag);

    // 删除标签
    void deleteTag(Long id);

    // 查询文章关联的标签
    List<Tag> getTagsByArticleId(Long articleId);

    // 为文章添加标签
    void addTagToArticle(Long articleId, Long tagId);

    // 删除文章的所有标签
    void removeAllTagsFromArticle(Long articleId);

    // 删除文章的特定标签
    void removeTagFromArticle(Long articleId, Long tagId);

    // 获取热门标签
    List<Map<String, Object>> getHotTags();

    // ========== 分类管理功能 ==========

    // 获取所有分类（去重）
    List<String> getAllCategories();

    // 更新文章分类
    void updateArticleCategory(Long id, String category);
}