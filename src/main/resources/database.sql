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

-- 创建新闻信息表，主键自增
CREATE TABLE IF NOT EXISTS news (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT DEFAULT NULL,
    image_src VARCHAR(200) DEFAULT NULL,
    source VARCHAR(50) DEFAULT NULL,
    type VARCHAR(20) DEFAULT 'new',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);