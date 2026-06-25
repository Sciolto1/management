/*!40101 SET NAMES utf8mb4 */;

-- ============ 完全重置数据库 ============
DROP DATABASE IF EXISTS `hr_management`;
CREATE DATABASE IF NOT EXISTS `hr_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hr_management`;

-- ============ 1. 部门表 ============
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `manager` VARCHAR(50) DEFAULT NULL COMMENT '部门负责人',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '部门描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

INSERT INTO `department` (`id`, `name`, `manager`, `description`) VALUES
(1, '技术部', '张三', '负责公司技术研发工作'),
(2, '人事部', '王五', '负责人力资源管理'),
(3, '财务部', NULL, '负责财务核算与管理'),
(4, '市场部', NULL, '负责市场推广与营销');

-- ============ 2. 员工表 ============
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `department` VARCHAR(50) NOT NULL COMMENT '部门',
  `position` VARCHAR(50) NOT NULL COMMENT '职位',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) DEFAULT NULL COMMENT '登录密码',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

INSERT INTO `employee` (`id`, `name`, `department`, `position`, `phone`, `email`, `hire_date`, `username`, `password`, `role`) VALUES
(1, '张三', '技术部', '前端工程师', '13800138001', 'zhangsan@company.com', '2023-01-15', 'user1', 'user1', 'dept_admin'),
(2, '李四', '技术部', '后端工程师', '13800138002', 'lisi@company.com', '2023-03-20', 'user2', 'user2', 'user'),
(3, '王五', '人事部', 'HR专员', '13800138003', 'wangwu@company.com', '2022-08-10', 'deptadmin1', 'deptadmin1', 'dept_admin'),
(4, '系统管理员', '管理层', '系统管理员', '', '', '2020-01-01', 'admin', 'admin', 'admin');

-- ============ 3. 通知公告表 ============
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `author` VARCHAR(50) NOT NULL COMMENT '发布人',
  `publish_date` DATE DEFAULT NULL COMMENT '发布日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

INSERT INTO `notice` (`id`, `title`, `content`, `author`, `publish_date`) VALUES
(1, '2026年元旦放假通知', '根据国家法定节假日安排，2026年元旦放假时间为1月1日至1月3日，共3天。请各部门提前做好工作安排。', '人事部', '2025-12-25'),
(2, '公司年会通知', '公司将于2026年1月20日举办年度总结大会暨年会，届时将进行年度优秀员工表彰，请全体员工准时参加。', '行政部', '2026-01-05');

-- ============ 4. 请假申请表 ============
DROP TABLE IF EXISTS `leave_request`;
CREATE TABLE `leave_request` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '请假ID',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `type` VARCHAR(20) NOT NULL COMMENT '请假类型',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '请假原因',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';

INSERT INTO `leave_request` (`id`, `employee_id`, `employee_name`, `type`, `start_date`, `end_date`, `reason`, `status`) VALUES
(1, 1, '张三', 'annual', '2026-01-10', '2026-01-12', '回老家探亲', 'pending');

-- ============ 5. 考勤记录表 ============
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '考勤ID',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `date` DATE NOT NULL COMMENT '日期',
  `clock_in` TIME DEFAULT NULL COMMENT '上班打卡时间',
  `clock_out` TIME DEFAULT NULL COMMENT '下班打卡时间',
  `status` VARCHAR(20) DEFAULT 'normal' COMMENT '考勤状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_date` (`employee_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';

INSERT INTO `attendance` (`id`, `employee_id`, `date`, `clock_in`, `clock_out`, `status`) VALUES
(1, 1, '2026-01-06', '09:00:00', '18:00:00', 'normal'),
(2, 2, '2026-01-06', '08:55:00', '18:30:00', 'normal');

-- ============ 6. 工资记录表 ============
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '工资ID',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `month` VARCHAR(7) NOT NULL COMMENT '月份',
  `base_salary` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '基本工资',
  `bonus` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '奖金',
  `deduction` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '扣款',
  `actual_salary` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实发工资',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工资记录表';

INSERT INTO `salary` (`id`, `employee_id`, `employee_name`, `month`, `base_salary`, `bonus`, `deduction`, `actual_salary`, `status`, `remark`) VALUES
(1, 1, '张三', '2026-01', 8000.00, 1500.00, 500.00, 9000.00, 'paid', '全勤奖励'),
(2, 2, '李四', '2026-01', 7500.00, 1000.00, 300.00, 8200.00, 'paid', NULL);

-- ============ 7. 绩效考核表 ============
DROP TABLE IF EXISTS `performance`;
CREATE TABLE `performance` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '绩效ID',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `period` VARCHAR(20) NOT NULL COMMENT '考核周期',
  `work_score` INT NOT NULL DEFAULT 0 COMMENT '工作能力得分',
  `attitude_score` INT NOT NULL DEFAULT 0 COMMENT '工作态度得分',
  `teamwork_score` INT NOT NULL DEFAULT 0 COMMENT '团队协作得分',
  `innovation_score` INT NOT NULL DEFAULT 0 COMMENT '创新能力得分',
  `total_score` INT NOT NULL DEFAULT 0 COMMENT '综合得分',
  `level` VARCHAR(5) NOT NULL DEFAULT 'C' COMMENT '等级',
  `comment` VARCHAR(500) DEFAULT NULL COMMENT '评语',
  `evaluator` VARCHAR(50) DEFAULT NULL COMMENT '评估人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效考核表';

INSERT INTO `performance` (`id`, `employee_id`, `employee_name`, `period`, `work_score`, `attitude_score`, `teamwork_score`, `innovation_score`, `total_score`, `level`, `comment`, `evaluator`) VALUES
(1, 1, '张三', '2025-Q4', 85, 90, 88, 82, 86, 'A', '工作认真负责，技术能力强，团队协作良好', '部门经理');

-- ============ 8. 培训表 ============
DROP TABLE IF EXISTS `training`;
CREATE TABLE `training` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '培训ID',
  `title` VARCHAR(200) NOT NULL COMMENT '培训主题',
  `description` TEXT DEFAULT NULL COMMENT '培训描述',
  `trainer` VARCHAR(50) NOT NULL COMMENT '讲师',
  `date` DATE NOT NULL COMMENT '培训日期',
  `time` VARCHAR(50) NOT NULL COMMENT '培训时间',
  `location` VARCHAR(200) NOT NULL COMMENT '培训地点',
  `max_participants` INT NOT NULL DEFAULT 20 COMMENT '人数上限',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='培训表';

INSERT INTO `training` (`id`, `title`, `description`, `trainer`, `date`, `time`, `location`, `max_participants`) VALUES
(1, '新员工入职培训', '公司规章制度、企业文化介绍', '人力资源部', '2026-02-01', '09:00-12:00', '会议室A', 30),
(2, 'Java技术进阶培训', 'Spring Boot最新特性及实践', '技术部', '2026-02-15', '14:00-17:00', '培训中心', 25);

-- ============ 9. 培训报名表 ============
DROP TABLE IF EXISTS `training_participant`;
CREATE TABLE `training_participant` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `training_id` INT NOT NULL COMMENT '培训ID',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_employee` (`training_id`, `employee_id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='培训报名表';

INSERT INTO `training_participant` (`id`, `training_id`, `employee_id`) VALUES
(1, 1, 2),
(2, 1, 1);

-- ============ 10. 公积金账户表 ============
DROP TABLE IF EXISTS `fund`;
CREATE TABLE `fund` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '公积金ID',
  `account_no` VARCHAR(50) NOT NULL COMMENT '公积金账号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '部门',
  `salary` DECIMAL(10,2) DEFAULT 0 COMMENT '工资额',
  `personal_pay` DECIMAL(10,2) DEFAULT 0 COMMENT '个人缴额',
  `company_pay` DECIMAL(10,2) DEFAULT 0 COMMENT '单位缴额',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `balance` DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态(active-正常, sealed-封存, closed-注销)',
  `open_date` DATE DEFAULT NULL COMMENT '开户日期',
  `employee_id` INT DEFAULT NULL COMMENT '关联员工ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_no` (`account_no`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公积金账户表';

INSERT INTO `fund` (`id`, `account_no`, `name`, `gender`, `department`, `salary`, `personal_pay`, `company_pay`, `birth_date`, `balance`, `status`, `open_date`, `employee_id`) VALUES
(1, 'GJJ1705000001', '张三', '男', '技术部', 8000.00, 640.00, 640.00, '1990-05-15', 15360.00, 'active', '2023-01-15', 1),
(2, 'GJJ1705000002', '李四', '男', '技术部', 7500.00, 600.00, 600.00, '1991-08-20', 14400.00, 'active', '2023-03-20', 2),
(3, 'GJJ1705000003', '王五', '女', '人事部', 6500.00, 520.00, 520.00, '1992-03-10', 12480.00, 'active', '2022-08-10', 3);

-- ============ 11. 公积金帐务表 ============
DROP TABLE IF EXISTS `fund_transaction`;
CREATE TABLE `fund_transaction` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '帐务ID',
  `fund_id` INT NOT NULL COMMENT '公积金账户ID',
  `account_no` VARCHAR(50) NOT NULL COMMENT '公积金账号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `trans_date` DATE NOT NULL COMMENT '交易日期',
  `debit` DECIMAL(10,2) DEFAULT 0 COMMENT '借方（支出）',
  `credit` DECIMAL(10,2) DEFAULT 0 COMMENT '贷方（收入）',
  `balance` DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
  `type` VARCHAR(20) NOT NULL COMMENT '交易类型(deposit-缴存, withdrawal-提取, interest-利息, adjustment-调整, open-开户, seal-封存, unseal-解封)',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_fund_id` (`fund_id`),
  KEY `idx_trans_date` (`trans_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公积金帐务表';

INSERT INTO `fund_transaction` (`id`, `fund_id`, `account_no`, `name`, `trans_date`, `debit`, `credit`, `balance`, `type`, `remark`) VALUES
(1, 1, 'GJJ1705000001', '张三', '2023-01-15', 0.00, 0.00, 0.00, 'open', '开户'),
(2, 1, 'GJJ1705000001', '张三', '2023-02-01', 0.00, 1280.00, 1280.00, 'deposit', '月度缴存'),
(3, 1, 'GJJ1705000001', '张三', '2023-03-01', 0.00, 1280.00, 2560.00, 'deposit', '月度缴存'),
(4, 2, 'GJJ1705000002', '李四', '2023-03-20', 0.00, 0.00, 0.00, 'open', '开户'),
(5, 2, 'GJJ1705000002', '李四', '2023-04-01', 0.00, 1200.00, 1200.00, 'deposit', '月度缴存'),
(6, 3, 'GJJ1705000003', '王五', '2022-08-10', 0.00, 0.00, 0.00, 'open', '开户'),
(7, 3, 'GJJ1705000003', '王五', '2022-09-01', 0.00, 1040.00, 1040.00, 'deposit', '月度缴存');