package com.lhxcsdn.demo.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private String status; // 审核状态: PENDING(待审核), APPROVED(通过), REJECTED(拒绝)
    private String auditReason; // 审核原因
    private Long auditBy; // 审核人ID
    private LocalDateTime auditTime; // 审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String nickname; // 用户昵称（通过JOIN查询获取）
    private String auditByName; // 审核人昵称（通过JOIN查询获取）
}