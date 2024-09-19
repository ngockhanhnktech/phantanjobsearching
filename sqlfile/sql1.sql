create database quanlycongviec;
use quanlycongviec;
CREATE TABLE jobs (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('pending', 'in_progress', 'completed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO jobs (title, description, status) VALUES
('Thiết kế website', 'Thiết kế và phát triển giao diện trang web', 'in_progress'),
('Viết báo cáo', 'Viết báo cáo về tình hình kinh doanh', 'pending'),
('Kiểm thử phần mềm', 'Kiểm thử tính năng mới của phần mềm', 'completed');
