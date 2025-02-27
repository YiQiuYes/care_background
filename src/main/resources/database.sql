-- 新建数据库care
CREATE DATABASE IF NOT EXISTS care;

-- 创建用户表，主键自增
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    refresh_token TEXT DEFAULT NULL
);