package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long articleId;
    private LocalDateTime createTime;
}