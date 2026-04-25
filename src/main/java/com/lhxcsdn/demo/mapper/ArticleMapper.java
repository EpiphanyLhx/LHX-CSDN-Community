package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import com.lhxcsdn.demo.pojo.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {

    /**
     * 1. 首页文章列表查询
     * 按照创建时间降序排列，保证最新的文章排在最前面
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "ORDER BY a.create_time DESC")
    List<Article> findAll();

    /**
     * 2. 根据分类查询文章
     * 用于首页的 Tab 切换筛选（如 Java 专栏）
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE a.category = #{category} " +
            "ORDER BY a.create_time DESC")
    List<Article> findByCategory(String category);

    /**
     * 3. 文章详情查询
     * 根据 ID 获取单篇文章的全部信息
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE a.id = #{id}")
    Article findById(Long id);

    /**
     * 4. 更新点赞数
     * 每次调用该接口，数据库中的点赞数原子性 +1
     */
    @Update("UPDATE article SET like_count = like_count + 1 WHERE id = #{id}")
    int addLikeCount(Long id);

    /**
     * 5. 获取文章的所有评论
     * 通过 LEFT JOIN 关联用户表，一次性查出评论内容和评论人的昵称
     */
    @Select("SELECT c.*, u.nickname FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.article_id = #{articleId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> findCommentsByArticleId(Long articleId);

    /**
     * 6. 保存评论
     * 将用户在前端输入的评论内容存入数据库
     */
    @Insert("INSERT INTO comment(article_id, user_id, content, create_time) " +
            "VALUES(#{articleId}, #{userId}, #{content}, NOW())") // 修改为驼峰属性名
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(Comment comment);

    /**
     * 7. 发布新文章
     */
    @Insert("INSERT INTO article(title, content, summary, category, author_id, view_count, like_count, create_time) " +
            "VALUES(#{title}, #{content}, #{summary}, #{category}, #{authorId}, 0, 0, NOW())")
    int insertArticle(Article article);

    /**
     * 8. 查询指定用户的文章列表
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE a.author_id = #{authorId} " +
            "ORDER BY a.create_time DESC")
    List<Article> findByAuthorId(Long authorId);

    /**
     * 9. 全局搜索：根据标题或摘要匹配关键词
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE a.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR a.summary LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY a.create_time DESC")
    List<Article> searchArticles(String keyword);

    /**
     * 10. 获取热度排行榜文章
     * 按照浏览量(view_count)降序排列，只取前 10 条
     * 关联用户表获取作者昵称
     */
    @Select("SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "ORDER BY a.view_count DESC LIMIT 10")
    List<Article> findHotArticles();

    /**
     * 11. 删除文章（需要 sys:article:delete 权限）
     */
    @Delete("DELETE FROM article WHERE id = #{id}")
    int deleteArticleById(Long id);

    /**
     * 12. 删除评论（需要 sys:comment:delete 权限）
     */
    @Delete("DELETE FROM comment WHERE id = #{commentId}")
    int deleteCommentById(@Param("commentId") Long commentId);

    // ========== 管理员功能 ==========

    /**
     * 13. 管理员文章列表（带筛选）
     */
    @Select("<script>" +
            "SELECT a.*, u.nickname as authorName FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND a.status = #{status} </if>" +
            "<if test='category != null and category != \"\"'> AND a.category = #{category} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.summary LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "ORDER BY a.create_time DESC" +
            "</script>")
    List<Article> findArticlesForAdmin(@Param("status") String status, @Param("category") String category, @Param("keyword") String keyword);

    /**
     * 14. 更新文章状态
     */
    @Update("UPDATE article SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateArticleStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 15. 管理员评论列表（带筛选）
     */
    @Select("<script>" +
            "SELECT c.*, u.nickname, u2.nickname as auditByName FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user u2 ON c.audit_by = u2.id " +
            "WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND c.status = #{status} </if>" +
            "<if test='articleId != null'> AND c.article_id = #{articleId} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND c.content LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<Comment> findCommentsForAdmin(@Param("status") String status, @Param("articleId") Long articleId, @Param("keyword") String keyword);

    /**
     * 16. 更新评论审核状态
     */
    @Update("UPDATE comment SET status = #{status}, audit_reason = #{auditReason}, audit_by = #{auditBy}, audit_time = NOW() WHERE id = #{id}")
    int updateCommentStatus(@Param("id") Long id, @Param("status") String status, @Param("auditReason") String auditReason, @Param("auditBy") Long auditBy);

    /**
     * 17. 批量更新评论状态
     */
    @Update("<script>" +
            "UPDATE comment SET status = #{status}, audit_reason = #{auditReason}, audit_by = #{auditBy}, audit_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateCommentStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("auditReason") String auditReason, @Param("auditBy") Long auditBy);

    /**
     * 18. 获取文章统计（按状态分组）
     */
    @Select("SELECT status, COUNT(*) as count FROM article GROUP BY status")
    List<Map<String, Object>> getArticleStats();

    /**
     * 19. 获取评论统计（按状态分组）
     */
    @Select("SELECT status, COUNT(*) as count FROM comment GROUP BY status")
    List<Map<String, Object>> getCommentStats();

    // ========== 标签管理功能 ==========

    /**
     * 20. 查询所有标签
     */
    @Select("SELECT * FROM tag ORDER BY create_time DESC")
    List<Tag> findAllTags();

    /**
     * 21. 根据ID查询标签
     */
    @Select("SELECT * FROM tag WHERE id = #{id}")
    Tag findTagById(Long id);

    /**
     * 22. 根据名称查询标签
     */
    @Select("SELECT * FROM tag WHERE name = #{name}")
    Tag findTagByName(String name);

    /**
     * 23. 插入标签
     */
    @Insert("INSERT INTO tag(name, description, create_time) VALUES(#{name}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTag(Tag tag);

    /**
     * 24. 更新标签
     */
    @Update("UPDATE tag SET name = #{name}, description = #{description} WHERE id = #{id}")
    int updateTag(Tag tag);

    /**
     * 25. 删除标签
     */
    @Delete("DELETE FROM tag WHERE id = #{id}")
    int deleteTagById(Long id);

    /**
     * 26. 查询文章关联的标签
     */
    @Select("SELECT t.* FROM tag t INNER JOIN article_tag at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 27. 为文章添加标签
     */
    @Insert("INSERT INTO article_tag(article_id, tag_id, create_time) VALUES(#{articleId}, #{tagId}, NOW())")
    int insertArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    /**
     * 28. 删除文章的所有标签
     */
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteArticleTags(Long articleId);

    /**
     * 29. 删除文章的特定标签
     */
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId} AND tag_id = #{tagId}")
    int deleteArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    /**
     * 30. 获取热门标签（按使用次数排序）
     */
    @Select("SELECT t.*, COUNT(at.tag_id) as usage_count FROM tag t " +
            "LEFT JOIN article_tag at ON t.id = at.tag_id " +
            "GROUP BY t.id ORDER BY usage_count DESC LIMIT 20")
    List<Map<String, Object>> findHotTags();

    /**
     * 31. 更新文章（用于编辑）
     */
    @Update("UPDATE article SET title = #{title}, content = #{content}, summary = #{summary}, category = #{category}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateArticle(Article article);

    /**
     * 32. 获取所有分类（去重）
     */
    @Select("SELECT DISTINCT category FROM article WHERE category IS NOT NULL AND category != '' ORDER BY category")
    List<String> findAllCategories();

    /**
     * 33. 更新文章分类
     */
    @Update("UPDATE article SET category = #{category}, update_time = NOW() WHERE id = #{id}")
    int updateArticleCategory(@Param("id") Long id, @Param("category") String category);
}