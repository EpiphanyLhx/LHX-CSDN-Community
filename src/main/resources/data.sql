-- 初始化 RBAC 数据
-- 1. 插入角色
INSERT INTO role (code, name, description) VALUES
('SUPER_ADMIN', '超级管理员', '拥有系统所有权限的最高管理员'),
('CONTENT_ADMIN', '内容管理员', '负责文章、评论等内容管理'),
('USER_ADMIN', '用户管理员', '负责用户管理和系统日志清理'),
('USER', '普通用户', '普通注册用户，拥有基本权限')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- 2. 插入权限（根据需求：修改清理日志、删除用户、删除评论、删除文章）
INSERT INTO permission (code, name, description) VALUES
('sys:log:clean', '清理系统日志', '清理系统操作日志和错误日志'),
('sys:user:delete', '删除用户', '永久删除用户账号及相关数据'),
('sys:comment:delete', '删除评论', '删除任意用户的评论'),
('sys:article:delete', '删除文章', '删除任意用户的文章'),
('sys:user:view', '查看用户列表', '查看系统所有用户信息'),
('sys:role:assign', '分配角色', '为用户分配或更改角色'),
('sys:article:view', '查看文章列表', '查看系统所有文章信息'),
('sys:article:edit', '编辑文章', '编辑任意用户的文章'),
('sys:comment:view', '查看评论列表', '查看系统所有评论信息'),
('sys:comment:audit', '审核评论', '审核评论通过或拒绝'),
('sys:category:manage', '分类管理', '管理文章分类'),
('sys:tag:manage', '标签管理', '管理文章标签')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- 3. 为角色分配权限
-- SUPER_ADMIN 拥有所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p WHERE r.code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- CONTENT_ADMIN 拥有内容管理权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p 
WHERE r.code = 'CONTENT_ADMIN' 
AND p.code IN ('sys:comment:delete', 'sys:article:delete', 'sys:user:view', 'sys:article:view', 'sys:article:edit', 'sys:comment:view', 'sys:comment:audit', 'sys:category:manage', 'sys:tag:manage')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- USER_ADMIN 拥有用户管理和日志清理权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p 
WHERE r.code = 'USER_ADMIN' 
AND p.code IN ('sys:log:clean', 'sys:user:delete', 'sys:user:view', 'sys:role:assign')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- USER 普通用户只有查看权限（如果需要）
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p 
WHERE r.code = 'USER' 
AND p.code IN ('sys:user:view')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 4. 创建超级管理员账号（如果不存在）
-- 密码为 admin（使用 {noop} 前缀表示不加密，仅用于开发测试）
INSERT INTO user (username, password, nickname, avatar, fans_count, article_count, create_time, bio, location, profession, education, tags) 
SELECT 'admin', '{noop}admin', '超级管理员', '/avatar/default.png', 0, 0, NOW(), '系统最高管理员', '北京', '系统管理员', '计算机科学', '管理员,系统' 
WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin');

-- 5. 为管理员分配 SUPER_ADMIN 角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM user u, role r 
WHERE u.username = 'admin' AND r.code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);