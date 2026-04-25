package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    
    @Select("SELECT * FROM role WHERE code = #{code}")
    Role findByCode(String code);
    
    @Select("SELECT * FROM role WHERE id = #{id}")
    Role findById(Long id);
    
    @Select("SELECT r.* FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<Role> findByUserId(Long userId);
    
    @Select("SELECT * FROM role ORDER BY create_time DESC")
    List<Role> findAll();
    
    @Insert("INSERT INTO role(code, name, description) VALUES(#{code}, #{name}, #{description})")
    int insert(Role role);
    
    @Update("UPDATE role SET name = #{name}, description = #{description}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(Role role);
    
    @Delete("DELETE FROM role WHERE id = #{id}")
    int delete(Long id);
    
    // 分配角色给用户
    @Insert("INSERT INTO user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    // 移除用户的角色
    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    // 更新用户角色（先删除后添加）
    @Update("UPDATE user_role SET role_id = #{newRoleId} WHERE user_id = #{userId} AND role_id = #{oldRoleId}")
    int updateUserRole(@Param("userId") Long userId, @Param("oldRoleId") Long oldRoleId, @Param("newRoleId") Long newRoleId);
    
    // 检查用户是否拥有某个角色
    @Select("SELECT COUNT(*) FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}