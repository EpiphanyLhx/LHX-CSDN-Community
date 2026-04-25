package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String category;
    private Long authorId;
    private String authorName; // 作者昵称，通过JOIN查询获取
    private Integer viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private String status; // 文章状态: DRAFT(草稿), PUBLISHED(已发布), DELETED(已删除)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}