package com.lhxcsdn.demo.pojo.entity;

import lombok.Data;

@Data
public class Banner {
    private Integer id;
    private String imgUrl;
    private String linkUrl;
    private String title;
    private Integer sortOrder;
}