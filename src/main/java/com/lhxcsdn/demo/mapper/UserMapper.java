package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 检查用户名是否已存在
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    // 根据ID查找用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    // 获取所有用户（包含角色信息）
    @Select("SELECT u.*, r.code as role, r.name as roleName " +
            "FROM user u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "ORDER BY u.create_time DESC")
    List<User> findAllWithRole();

    // 插入新用户（注册）
    @Insert("INSERT INTO user(username, password, nickname) VALUES(#{username}, #{password}, #{nickname})")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET nickname=#{nickname}, bio=#{bio}, location=#{location}, " +
            "profession=#{profession}, education=#{education}, tags=#{tags}, avatar=#{avatar} WHERE id=#{id}")
    int updateUserInfo(User user);

    // 删除用户
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    // 查用户的角色 (关联 user_role 和 role 表)
    @Select("SELECT r.code FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> findRoleCodesByUserId(Long userId);

    // 查用户的具体权限 (关联 3 张表)
    @Select("SELECT p.code FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findPermissionCodesByUserId(Long userId);

    // 更新用户的角色
    @Update("UPDATE user_role SET role_id = (SELECT id FROM role WHERE code = #{roleCode}) WHERE user_id = #{userId}")
    int updateUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    // 检查用户是否拥有某个角色
    @Select("SELECT COUNT(*) FROM user_role ur INNER JOIN role r ON ur.role_id = r.id WHERE ur.user_id = #{userId} AND r.code = #{roleCode}")
    int hasRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);
}