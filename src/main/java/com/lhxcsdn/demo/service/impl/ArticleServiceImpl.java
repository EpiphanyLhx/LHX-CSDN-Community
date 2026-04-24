package com.lhxcsdn.demo.service.impl;

import com.lhxcsdn.demo.mapper.ArticleMapper;
import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // ServiceImpl 实现类
    @Override
    public void publishArticle(Article article) {
        // 为了方便测试，暂时固定一个 author_id，实际应从登录 Token 中获取
        article.setAuthorId(1L);
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
}