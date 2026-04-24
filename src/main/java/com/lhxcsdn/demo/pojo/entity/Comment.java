// Comment.java
package com.lhxcsdn.demo.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty; // 导入注解
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;

    @JsonProperty("article_id") // 对应前端传的 article_id
    private Long articleId;

    @JsonProperty("user_id") // 对应前端传的 user_id
    private Long userId;

    private String content;
    private LocalDateTime create_time;
    private String nickname;
}