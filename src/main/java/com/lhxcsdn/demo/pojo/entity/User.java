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
}