package com.lhxcsdn.demo.controller;

import com.lhxcsdn.demo.common.Result;
import com.lhxcsdn.demo.pojo.entity.User;
import com.lhxcsdn.demo.mapper.UserMapper;
import com.lhxcsdn.demo.service.UserService; // 引入 UserService
import com.lhxcsdn.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // ✨ 补上缺失的注册接口
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String msg = userService.register(user);
        if ("注册成功".equals(msg)) {
            return Result.success(msg);
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser == null) {
            // ✨ 修改点 2：根据你 Result 的定义，只传 String 消息
            return Result.error("用户不存在");
        }

        if (!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", dbUser.getId());
        claims.put("username", dbUser.getUsername());

        String token = JwtUtils.generateToken(claims);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", dbUser.getId());
        data.put("nickname", dbUser.getNickname());

        return Result.success(data);
    }

    /**
     * 获取个人资料（包含角色信息）
     */
    @GetMapping("/me")
    public Result getMe(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }

        try {
            String actualToken = token.substring(7);
            String username = JwtUtils.parseToken(actualToken).get("username", String.class);
            User user = userMapper.findByUsername(username);

            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 获取用户角色
            List<String> roles = userMapper.findRoleCodesByUserId(user.getId());
            String mainRole = roles.isEmpty() ? "USER" : roles.get(0);
            
            // 创建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("avatar", user.getAvatar());
            data.put("fansCount", user.getFansCount());
            data.put("articleCount", user.getArticleCount());
            data.put("createTime", user.getCreateTime());
            data.put("bio", user.getBio());
            data.put("location", user.getLocation());
            data.put("profession", user.getProfession());
            data.put("education", user.getEducation());
            data.put("tags", user.getTags());
            data.put("roles", roles);
            data.put("mainRole", mainRole);
            
            return Result.success(data);

        } catch (Exception e) {
            return Result.error("凭证已失效");
        }
    }

    /**
     * 更新个人资料
     */
    @PostMapping("/update")
    public Result updateProfile(@RequestBody User user) {
        // ✨ 这里必须确保传进来的 user 是你 pojo.entity 里的 User
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        int rows = userMapper.updateUserInfo(user);

        if (rows > 0) {
            return Result.success("资料更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file,
                               @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        try {
            String actualToken = token.substring(7);
            String username = JwtUtils.parseToken(actualToken).get("username", String.class);
            User user = userMapper.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 检查文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return Result.error("图片大小不能超过5MB");
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String newFilename = user.getId() + "_" + timestamp + fileExtension;
            
            // 创建上传目录（相对于项目根目录）
            String uploadDir = System.getProperty("user.dir") + "/uploads/avatar/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(uploadDir + newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL（相对路径）
            String avatarUrl = "/uploads/avatar/" + newFilename;
            
            // 更新用户头像字段
            user.setAvatar(avatarUrl);
            int rows = userMapper.updateUserInfo(user);
            if (rows > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("avatarUrl", avatarUrl);
                return Result.success("头像上传成功", data);
            } else {
                // 如果更新失败，删除已上传的文件
                Files.deleteIfExists(filePath);
                return Result.error("头像更新失败");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}