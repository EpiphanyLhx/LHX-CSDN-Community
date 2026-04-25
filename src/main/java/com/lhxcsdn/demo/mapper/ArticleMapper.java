package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.Article;
import com.lhxcsdn.demo.pojo.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 1. 首页文章列表查询
     * 按照创建时间降序排列，保证最新的文章排在最前面
     */
    @Select("SELECT * FROM article ORDER BY create_time DESC")
    List<Article> findAll();

    /**
     * 2. 根据分类查询文章
     * 用于首页的 Tab 切换筛选（如 Java 专栏）
     */
    @Select("SELECT * FROM article WHERE category = #{category} ORDER BY create_time DESC")
    List<Article> findByCategory(String category);

    /**
     * 3. 文章详情查询
     * 根据 ID 获取单篇文章的全部信息
     */
    @Select("SELECT * FROM article WHERE id = #{id}")
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
     */
    @Select("SELECT * FROM article WHERE author_id = #{authorId} ORDER BY create_time DESC")
    List<Article> findByAuthorId(Long authorId);

    /**
     * 9. 全局搜索：根据标题或摘要匹配关键词
     */
    @Select("SELECT * FROM article WHERE title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR summary LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY create_time DESC")
    List<Article> searchArticles(String keyword);

    /**
     * 10. 获取热度排行榜文章
     * 按照浏览量(view_count)降序排列，只取前 10 条
     */
    @Select("SELECT * FROM article ORDER BY view_count DESC LIMIT 10")
    List<Article> findHotArticles();
}