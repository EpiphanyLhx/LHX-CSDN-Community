package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 1. 获取文章列表
     * 用于首页展示
     */
    @GetMapping("/list")
    public Result getList() {
        return Result.success(articleService.getAllArticles());
    }

    /**
     * 2. 获取文章详情
     * 用于详情页展示，包含正文内容
     * @param id 文章ID
     */
    @GetMapping("/{id}")
    public Result getDetail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }

    /**
     * 3. 获取文章
     * @param id 文章ID
     */
    @GetMapping("/{id}/comments")
    public Result getComments(@PathVariable Long id) {
        List<Comment> comments = articleService.getCommentsByArticleId(id);
        return Result.success(comments);
    }
    // 获取指定用户的文章列表
    @GetMapping("/user/{authorId}")
    public Result getUserArticles(@PathVariable Long authorId) {
        return Result.success(articleService.getArticlesByAuthor(authorId));
    }

    /**
     * 4. 提交评论
     * @param comment 评论实体（包含 article_id, user_id, content）
     */
    @PostMapping("/comment")
    public Result addComment(@RequestBody Comment comment) {
        // 简单校验：评论内容不能为空
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        articleService.saveComment(comment);
        return Result.success("评论成功");
    }

    /**
     * 5. 文章点赞
     * 每次调用该接口，对应文章的点赞数自增1
     * @param id 文章ID
     */
    @PostMapping("/{id}/like")
    public Result likeArticle(@PathVariable Long id) {
        articleService.addLike(id);
        return Result.success("点赞成功");
    }

    // 6. 发布文章
    @PostMapping("/publish")
    public Result publish(@RequestBody Article article) {
        articleService.publishArticle(article);
        return Result.success("发布成功");
    }

    // 7. 全局搜索文章
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        return Result.success(articleService.search(keyword));
    }


}