package com.lhxcsdn.demo.service.impl;

import com.lhxcsdn.demo.mapper.ArticleMapper;
import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.pojo.entity.Tag;
import com.lhxcsdn.demo.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public List<Article> getAllArticles() {
        return articleMapper.findAll();
    }

    @Override
    public Article getById(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return articleMapper.findCommentsByArticleId(articleId);
    }

    @Override
    public void saveComment(Comment comment) {
        articleMapper.insertComment(comment);
    }

    @Override
    public void addLike(Long id) {
        articleMapper.addLikeCount(id);
    }

    @Override
    public List<Article> getHotArticles() {
        return articleMapper.findHotArticles();
    }

    // ServiceImpl 实现类
    @Override
    public void publishArticle(Article article) {

        // 自动截取正文前 50 个字作为摘要
        if(article.getSummary() == null && article.getContent() != null){
            String summary = article.getContent().length() > 50 ? article.getContent().substring(0, 50) + "..." : article.getContent();
            article.setSummary(summary);
        }
        articleMapper.insertArticle(article);
    }

    //个人中心
    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleMapper.findByAuthorId(authorId);
    }

    @Override
    public List<Article> search(String keyword) {
        return articleMapper.searchArticles(keyword);
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteArticleById(id);
    }

    @Override
    public void deleteComment(Long commentId) {
        articleMapper.deleteCommentById(commentId);
    }

    // ========== 管理员功能实现 ==========

    @Override
    public List<Article> getArticlesForAdmin(String status, String category, String keyword) {
        return articleMapper.findArticlesForAdmin(status, category, keyword);
    }

    @Override
    public void updateArticleStatus(Long id, String status) {
        articleMapper.updateArticleStatus(id, status);
    }

    @Override
    public List<Comment> getCommentsForAdmin(String status, Long articleId, String keyword) {
        return articleMapper.findCommentsForAdmin(status, articleId, keyword);
    }

    @Override
    public void updateCommentStatus(Long id, String status, String auditReason, Long auditBy) {
        articleMapper.updateCommentStatus(id, status, auditReason, auditBy);
    }

    @Override
    public void batchUpdateCommentStatus(List<Long> ids, String status, String auditReason, Long auditBy) {
        articleMapper.batchUpdateCommentStatus(ids, status, auditReason, auditBy);
    }

    @Override
    public List<Map<String, Object>> getArticleStats() {
        return articleMapper.getArticleStats();
    }

    @Override
    public List<Map<String, Object>> getCommentStats() {
        return articleMapper.getCommentStats();
    }

    @Override
    public void updateArticle(Article article) {
        articleMapper.updateArticle(article);
    }

    // ========== 标签管理功能实现 ==========

    @Override
    public List<Tag> getAllTags() {
        return articleMapper.findAllTags();
    }

    @Override
    public Tag getTagById(Long id) {
        return articleMapper.findTagById(id);
    }

    @Override
    public Tag getTagByName(String name) {
        return articleMapper.findTagByName(name);
    }

    @Override
    public void createTag(Tag tag) {
        articleMapper.insertTag(tag);
    }

    @Override
    public void updateTag(Tag tag) {
        articleMapper.updateTag(tag);
    }

    @Override
    public void deleteTag(Long id) {
        articleMapper.deleteTagById(id);
    }

    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        return articleMapper.findTagsByArticleId(articleId);
    }

    @Override
    public void addTagToArticle(Long articleId, Long tagId) {
        articleMapper.insertArticleTag(articleId, tagId);
    }

    @Override
    public void removeAllTagsFromArticle(Long articleId) {
        articleMapper.deleteArticleTags(articleId);
    }

    @Override
    public void removeTagFromArticle(Long articleId, Long tagId) {
        articleMapper.deleteArticleTag(articleId, tagId);
    }

    @Override
    public List<Map<String, Object>> getHotTags() {
        return articleMapper.findHotTags();
    }

    // ========== 分类管理功能实现 ==========

    @Override
    public List<String> getAllCategories() {
        return articleMapper.findAllCategories();
    }

    @Override
    public void updateArticleCategory(Long id, String category) {
        articleMapper.updateArticleCategory(id, category);
    }
}