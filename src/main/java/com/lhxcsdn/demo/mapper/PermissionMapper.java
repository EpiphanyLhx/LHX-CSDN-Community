package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {
    
    @Select("SELECT * FROM permission WHERE code = #{code}")
    Permission findByCode(String code);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> findByUserId(Long userId);
    
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> findByRoleId(Long roleId);
    
    @Select("SELECT * FROM permission ORDER BY create_time DESC")
    List<Permission> findAll();
}