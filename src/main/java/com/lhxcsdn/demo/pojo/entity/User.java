package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data // 自动生成 Getter/Setter/ToString
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private Integer fansCount;
    private Integer articleCount;
    private LocalDateTime createTime;
    private String bio;
    private String location;
    private String profession;
    private String education;
    private String tags;
    
    // 以下字段仅用于查询结果映射，不持久化到数据库
    private String role;       // 角色代码（用于前端显示）
    private String roleName;   // 角色名称
    private String roleCode;   // 角色代码（别名，与role相同）
}