package com.lhxcsdn.demo.mapper;

import com.lhxcsdn.demo.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    // 检查用户名是否已存在
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    // 插入新用户（注册）
    @Insert("INSERT INTO user(username, password, nickname) VALUES(#{username}, #{password}, #{nickname})")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET nickname=#{nickname}, bio=#{bio}, location=#{location}, " +
            "profession=#{profession}, education=#{education}, tags=#{tags} WHERE id=#{id}")
    int updateUserInfo(User user);



    // 查用户的角色 (关联 user_role 和 role 表)
    @Select("SELECT r.code FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> findRoleCodesByUserId(Long userId);

    // 查用户的具体权限 (关联 3 张表)
    @Select("SELECT p.code FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findPermissionCodesByUserId(Long userId);
}