package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.pojo.entity.Tag;
import com.lhxcsdn.demo.service.ArticleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;
    private final UserMapper userMapper;

    public AdminArticleController(ArticleService articleService, UserMapper userMapper) {
        this.articleService = articleService;
        this.userMapper = userMapper;
    }

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            com.lhxcsdn.demo.pojo.entity.User user = userMapper.findByUsername(username);
            if (user != null) {
                return user.getId();
            }
        }
        return null;
    }

    // ========== 文章管理 ==========

    /**
     * 获取文章列表（管理员）
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('sys:article:view', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getArticles(@RequestParam(required = false) String status,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) String keyword) {
        List<Article> articles = articleService.getArticlesForAdmin(status, category, keyword);
        return Result.success(articles);
    }

    /**
     * 更新文章状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('sys:article:edit', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        if (status == null || !List.of("DRAFT", "PUBLISHED", "DELETED").contains(status)) {
            return Result.error("状态值无效");
        }
        articleService.updateArticleStatus(id, status);
        return Result.success("文章状态更新成功");
    }

    /**
     * 编辑文章
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sys:article:edit', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result updateArticle(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        articleService.updateArticle(article);
        return Result.success("文章更新成功");
    }

    /**
     * 删除文章（物理删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sys:article:delete', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("文章删除成功");
    }

    /**
     * 获取文章统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('sys:article:view', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getArticleStats() {
        List<Map<String, Object>> stats = articleService.getArticleStats();
        return Result.success(stats);
    }

    // ========== 评论管理 ==========

    /**
     * 获取评论列表（管理员）
     */
    @GetMapping("/comments")
    @PreAuthorize("hasAnyAuthority('sys:comment:view', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getComments(@RequestParam(required = false) String status,
                              @RequestParam(required = false) Long articleId,
                              @RequestParam(required = false) String keyword) {
        List<Comment> comments = articleService.getCommentsForAdmin(status, articleId, keyword);
        return Result.success(comments);
    }

    /**
     * 审核评论（单个）
     */
    @PutMapping("/comments/{id}/audit")
    @PreAuthorize("hasAnyAuthority('sys:comment:audit', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result auditComment(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        String auditReason = params.get("auditReason");
        Long auditBy = getCurrentUserId();
        if (auditBy == null) {
            return Result.error("无法获取当前用户信息");
        }
        if (status == null || !List.of("APPROVED", "REJECTED").contains(status)) {
            return Result.error("状态值无效");
        }
        articleService.updateCommentStatus(id, status, auditReason, auditBy);
        return Result.success("评论审核完成");
    }

    /**
     * 批量审核评论
     */
    @PutMapping("/comments/batch-audit")
    @PreAuthorize("hasAnyAuthority('sys:comment:audit', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result batchAuditComments(@RequestBody Map<String, Object> params) {
        List<Long> ids = (List<Long>) params.get("ids");
        String status = (String) params.get("status");
        String auditReason = (String) params.get("auditReason");
        Long auditBy = getCurrentUserId();
        if (auditBy == null) {
            return Result.error("无法获取当前用户信息");
        }
        if (status == null || !List.of("APPROVED", "REJECTED").contains(status)) {
            return Result.error("状态值无效");
        }
        articleService.batchUpdateCommentStatus(ids, status, auditReason, auditBy);
        return Result.success("批量审核完成");
    }

    /**
     * 获取评论统计
     */
    @GetMapping("/comments/stats")
    @PreAuthorize("hasAnyAuthority('sys:comment:view', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getCommentStats() {
        List<Map<String, Object>> stats = articleService.getCommentStats();
        return Result.success(stats);
    }

    // ========== 标签管理 ==========

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getTags() {
        List<Tag> tags = articleService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/tags/hot")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getHotTags() {
        List<Map<String, Object>> hotTags = articleService.getHotTags();
        return Result.success(hotTags);
    }

    /**
     * 创建标签
     */
    @PostMapping("/tags")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result createTag(@RequestBody Tag tag) {
        articleService.createTag(tag);
        return Result.success("标签创建成功");
    }

    /**
     * 更新标签
     */
    @PutMapping("/tags/{id}")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        articleService.updateTag(tag);
        return Result.success("标签更新成功");
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/tags/{id}")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result deleteTag(@PathVariable Long id) {
        articleService.deleteTag(id);
        return Result.success("标签删除成功");
    }

    /**
     * 获取文章关联的标签
     */
    @GetMapping("/{articleId}/tags")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getArticleTags(@PathVariable Long articleId) {
        List<Tag> tags = articleService.getTagsByArticleId(articleId);
        return Result.success(tags);
    }

    /**
     * 为文章添加标签
     */
    @PostMapping("/{articleId}/tags/{tagId}")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result addTagToArticle(@PathVariable Long articleId, @PathVariable Long tagId) {
        articleService.addTagToArticle(articleId, tagId);
        return Result.success("标签添加成功");
    }

    /**
     * 删除文章的标签
     */
    @DeleteMapping("/{articleId}/tags/{tagId}")
    @PreAuthorize("hasAnyAuthority('sys:tag:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result removeTagFromArticle(@PathVariable Long articleId, @PathVariable Long tagId) {
        articleService.removeTagFromArticle(articleId, tagId);
        return Result.success("标签移除成功");
    }

    // ========== 分类管理 ==========

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyAuthority('sys:category:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result getCategories() {
        List<String> categories = articleService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 更新文章分类
     */
    @PutMapping("/{id}/category")
    @PreAuthorize("hasAnyAuthority('sys:category:manage', 'SUPER_ADMIN', 'CONTENT_ADMIN')")
    public Result updateArticleCategory(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String category = params.get("category");
        articleService.updateArticleCategory(id, category);
        return Result.success("分类更新成功");
    }
}