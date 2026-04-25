package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Permission {
    private Long id;
    private String code;        // 权限代码，如 sys:user:delete
    private String name;        // 权限名称，如 删除用户
    private String description; // 权限描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}