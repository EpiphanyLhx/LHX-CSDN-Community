package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    private Long id;
    private String code;        // 角色代码，如 SUPER_ADMIN
    private String name;        // 角色名称，如 超级管理员
    private String description; // 角色描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}