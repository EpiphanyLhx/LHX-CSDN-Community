package com.lhxcsdn.demo.service;

import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;

import java.util.List;

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
}