package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long article_id; // 对应数据库字段
    private Long user_id;
    private String content;
    private LocalDateTime create_time;
    private String nickname;
}