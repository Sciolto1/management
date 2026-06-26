/*!40101 SET NAMES utf8mb4 */;

-- ============================================
-- 公司人事管理系统 - 完整数据库脚本
-- ============================================

-- ============ 完全重置数据库 ============
DROP DATABASE IF EXISTS `hr_management`;
CREATE DATABASE IF NOT EXISTS `hr_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hr_management`;

-- ============================================
-- 1. 部门表
-- ============================================
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `dept_code` VARCHAR(20) DEFAULT NULL COMMENT '部门编码',
  `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `manager` VARCHAR(50) DEFAULT NULL COMMENT '部门负责人',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '部门描述',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态(active-正常, inactive-停用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

INSERT INTO `department` (`id`, `dept_code`, `name`, `manager`, `description`, `remark`, `status`) VALUES
(1, 'DEPT001', '技术部', '张三', '负责公司技术研发工作', '核心研发部门', 'active'),
(2, 'DEPT002', '人事部', '王五', '负责人力资源管理', '综合管理处', 'active'),
(3, 'DEPT003', '财务部', NULL, '负责财务核算与管理', NULL, 'active'),
(4, 'DEPT004', '市场部', NULL, '负责市场推广与营销', NULL, 'active'),
(5, 'DEPT005', '生产车间', NULL, '负责产品生产制造', '一线生产部门', 'active');

-- ============================================
-- 2. 员工表
-- ============================================
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `employee_no` VARCHAR(20) DEFAULT NULL COMMENT '职工编号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `photo` VARCHAR(255) DEFAULT NULL COMMENT '照片',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `ethnicity` VARCHAR(20) DEFAULT NULL COMMENT '民族',
  `political_status` VARCHAR(20) DEFAULT NULL COMMENT '政治面貌',
  `marital_status` VARCHAR(10) DEFAULT NULL COMMENT '婚否',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `department` VARCHAR(50) NOT NULL COMMENT '部门',
  `position` VARCHAR(50) NOT NULL COMMENT '职位',
  `team_group` VARCHAR(50) DEFAULT NULL COMMENT '班组',
  `employee_category` VARCHAR(20) DEFAULT NULL COMMENT '人员类别(worker-工人, cadre-干部, temp-临时工)',
  `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
  `major` VARCHAR(50) DEFAULT NULL COMMENT '专业',
  `education` VARCHAR(20) DEFAULT NULL COMMENT '学历',
  `graduation_school` VARCHAR(100) DEFAULT NULL COMMENT '毕业学校',
  `graduation_date` DATE DEFAULT NULL COMMENT '毕业时间',
  `base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `entry_date` DATE DEFAULT NULL COMMENT '入厂时间',
  `work_start_date` DATE DEFAULT NULL COMMENT '参加工作时间',
  `contract_start_date` DATE DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` DATE DEFAULT NULL COMMENT '合同结束日期',
  `home_address` VARCHAR(200) DEFAULT NULL COMMENT '家庭住址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态(active-在职, left-离职, retired-退休)',
  `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) DEFAULT NULL COMMENT '登录密码',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_department` (`department`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

INSERT INTO `employee` (`id`, `employee_no`, `name`, `age`, `gender`, `birth_date`, `ethnicity`, `political_status`, `marital_status`, `id_card`, `department`, `position`, `team_group`, `employee_category`, `title`, `major`, `education`, `graduation_school`, `graduation_date`, `base_salary`, `phone`, `email`, `entry_date`, `work_start_date`, `contract_start_date`, `contract_end_date`, `home_address`, `status`, `hire_date`, `username`, `password`, `role`) VALUES
(1, 'EMP0001', '张三', 32, '男', '1990-05-15', '汉族', '党员', '已婚', '110101199005151234', '技术部', '前端工程师', '前端组', 'cadre', '工程师', '计算机科学', '本科', '清华大学', '2012-07-01', 8000.00, '13800138001', 'zhangsan@company.com', '2023-01-15', '2012-07-01', '2023-01-15', '2026-01-14', '北京市朝阳区建国路1号', 'active', '2023-01-15', 'user1', 'user1', 'dept_admin'),
(2, 'EMP0002', '李四', 30, '男', '1991-08-20', '汉族', '群众', '未婚', '110101199108202345', '技术部', '后端工程师', '后端组', 'worker', '助理工程师', '软件工程', '本科', '北京大学', '2014-07-01', 7500.00, '13800138002', 'lisi@company.com', '2023-03-20', '2014-07-01', '2023-03-20', '2026-03-19', '北京市海淀区中关村大街2号', 'active', '2023-03-20', 'user2', 'user2', 'user'),
(3, 'EMP0003', '王五', 31, '女', '1992-03-10', '汉族', '团员', '已婚', '110101199203103456', '人事部', 'HR专员', '人事组', 'cadre', '经济师', '人力资源', '本科', '复旦大学', '2014-07-01', 6500.00, '13800138003', 'wangwu@company.com', '2022-08-10', '2014-07-01', '2022-08-10', '2025-08-09', '上海市浦东新区张江路3号', 'active', '2022-08-10', 'deptadmin1', 'deptadmin1', 'dept_admin'),
(4, 'ADMIN01', '系统管理员', 35, '男', '1988-01-01', '汉族', '党员', '已婚', '110101198801011234', '管理层', '系统管理员', NULL, 'cadre', '高级工程师', '计算机科学', '本科', '北京航空航天大学', '2010-07-01', 12000.00, '13800138000', 'admin@company.com', '2020-01-01', '2010-07-01', '2020-01-01', '2030-12-31', '北京市西城区金融街1号', 'active', '2020-01-01', 'admin', 'admin', 'admin'),
(5, 'EMP0004', '赵六', 28, '男', '1995-06-25', '汉族', '群众', '未婚', '110101199506254567', '财务部', '会计', '财务组', 'cadre', '会计师', '财务管理', '本科', '中央财经大学', '2017-07-01', 7000.00, '13800138004', 'zhaoliu@company.com', '2023-05-10', '2017-07-01', '2023-05-10', '2026-05-09', '北京市西城区西直门4号', 'active', '2023-05-10', 'user3', 'user3', 'user'),
(6, 'EMP0005', '钱七', 26, '女', '1997-11-08', '汉族', '群众', '未婚', '110101199711085678', '市场部', '市场专员', '市场组', 'worker', '助理营销师', '市场营销', '大专', '对外经济贸易大学', '2019-07-01', 5500.00, '13800138005', 'qianqi@company.com', '2023-06-01', '2019-07-01', '2023-06-01', '2026-05-31', '北京市丰台区方庄5号', 'active', '2023-06-01', 'user4', 'user4', 'user'),
(7, 'EMP0006', '孙八', 45, '男', '1978-04-12', '汉族', '党员', '已婚', '110101197804126789', '生产车间', '车间主任', '装配一组', 'cadre', '高级工程师', '机械工程', '本科', '哈尔滨工业大学', '2000-07-01', 9500.00, '13800138006', 'sunba@company.com', '2021-03-15', '2000-07-01', '2021-03-15', '2026-03-14', '北京市通州区运河6号', 'active', '2021-03-15', 'user5', 'user5', 'dept_admin'),
(8, 'EMP0007', '周九', 38, '男', '1985-09-30', '汉族', '群众', '已婚', '110101198509307890', '生产车间', '技术工人', '装配二组', 'worker', '高级技师', '机械制造', '高中', '北京市工业技工学校', '2003-07-01', 6500.00, '13800138007', 'zhoujiu@company.com', '2022-01-10', '2003-07-01', '2022-01-10', '2025-01-09', '北京市大兴区黄村7号', 'active', '2022-01-10', 'user6', 'user6', 'user'),
(9, 'TEMP0001', '吴十', 24, '女', '2000-02-14', '回族', '群众', '未婚', '110101200002149012', '生产车间', '临时工', '包装组', 'temp', NULL, '电子商务', '大专', '北京城市学院', '2022-07-01', 4000.00, '13800138008', 'wushi@company.com', '2024-01-05', '2022-07-01', '2024-01-05', '2024-12-31', '北京市昌平区回龙观8号', 'active', '2024-01-05', 'user7', 'user7', 'user');

-- ============================================
-- 3. 通知公告表
-- ============================================
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
(2, '公司年会通知', '公司将于2026年1月20日举办年度总结大会暨年会，届时将进行年度优秀员工表彰，请全体员工准时参加。', '行政部', '2026-01-05'),
(3, '部门人员管理优化公告', '公司人事管理系统已优化部门人员管理模块，新增人员档案、工种分类、合同期限等20余项字段，详情请联系人事部。', '人事部', '2026-01-10');

-- ============================================
-- 4. 请假申请表
-- ============================================
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
(1, 1, '张三', 'annual', '2026-01-10', '2026-01-12', '回老家探亲', 'pending'),
(2, 6, '钱七', 'sick', '2026-01-08', '2026-01-09', '感冒发烧', 'approved');

-- ============================================
-- 5. 考勤记录表
-- ============================================
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
(2, 2, '2026-01-06', '08:55:00', '18:30:00', 'normal'),
(3, 3, '2026-01-06', '08:45:00', '18:15:00', 'normal'),
(4, 1, '2026-01-07', '09:05:00', '18:00:00', 'late');

-- ============================================
-- 6. 工资记录表
-- ============================================
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
(2, 2, '李四', '2026-01', 7500.00, 1000.00, 300.00, 8200.00, 'paid', NULL),
(3, 3, '王五', '2026-01', 6500.00, 800.00, 200.00, 7100.00, 'paid', NULL),
(4, 7, '孙八', '2026-01', 9500.00, 2000.00, 600.00, 10900.00, 'paid', '车间管理奖励');

-- ============================================
-- 7. 绩效考核表
-- ============================================
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
(1, 1, '张三', '2025-Q4', 85, 90, 88, 82, 86, 'A', '工作认真负责，技术能力强，团队协作良好', '部门经理'),
(2, 2, '李四', '2025-Q4', 80, 85, 90, 75, 83, 'B', '后端开发能力强，沟通能力优秀', '部门经理'),
(3, 7, '孙八', '2025-Q4', 92, 88, 85, 80, 87, 'A', '车间管理经验丰富，生产效率提升显著', '生产总监');

-- ============================================
-- 8. 培训表
-- ============================================
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
(2, 'Java技术进阶培训', 'Spring Boot最新特性及实践', '技术部', '2026-02-15', '14:00-17:00', '培训中心', 25),
(3, '安全生产培训', '车间安全生产规范及应急处理', '安全部', '2026-02-20', '09:00-12:00', '车间会议室', 50);

-- ============================================
-- 9. 培训报名表
-- ============================================
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
(2, 1, 1),
(3, 2, 1),
(4, 2, 2),
(5, 3, 7),
(6, 3, 8);

-- ============================================
-- 10. 人员调动记录表
-- ============================================
DROP TABLE IF EXISTS `transfer_record`;
CREATE TABLE `transfer_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '调动记录ID',
  `transfer_no` VARCHAR(30) DEFAULT NULL COMMENT '调动单号',
  `employee_id` INT NOT NULL COMMENT '员工ID',
  `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `employee_no` VARCHAR(20) DEFAULT NULL COMMENT '职工编号',
  `transfer_type` VARCHAR(20) NOT NULL COMMENT '调动类型(internal-内部调动, external_in-外单位调入, external_out-调出外单位, retire-离退休, dismiss-除名)',
  `source_department` VARCHAR(50) DEFAULT NULL COMMENT '原部门',
  `target_department` VARCHAR(50) DEFAULT NULL COMMENT '目标部门',
  `source_position` VARCHAR(50) DEFAULT NULL COMMENT '原职位',
  `target_position` VARCHAR(50) DEFAULT NULL COMMENT '目标职位',
  `source_team_group` VARCHAR(50) DEFAULT NULL COMMENT '原班组',
  `target_team_group` VARCHAR(50) DEFAULT NULL COMMENT '目标班组',
  `source_employee_category` VARCHAR(20) DEFAULT NULL COMMENT '原人员类别',
  `target_employee_category` VARCHAR(20) DEFAULT NULL COMMENT '目标人员类别',
  `source_base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '原基本工资',
  `target_base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '目标基本工资',
  `transfer_date` DATE DEFAULT NULL COMMENT '调动日期',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '调动原因',
  `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态(pending-待审批, approved-已通过, rejected-已驳回)',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_transfer_type` (`transfer_type`),
  KEY `idx_status` (`status`),
  KEY `idx_transfer_date` (`transfer_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员调动记录表';

INSERT INTO `transfer_record` (`id`, `transfer_no`, `employee_id`, `employee_name`, `employee_no`, `transfer_type`, `source_department`, `target_department`, `source_position`, `target_position`, `source_team_group`, `target_team_group`, `source_employee_category`, `target_employee_category`, `source_base_salary`, `target_base_salary`, `transfer_date`, `reason`, `approver`, `status`, `remark`) VALUES
(1, 'TR20260105001', 2, '李四', 'EMP0002', 'internal', '技术部', '技术部', '后端工程师', '高级后端工程师', '后端组', '后端组', 'worker', 'cadre', 7500.00, 9000.00, '2026-01-05', '技术能力突出，晋升为高级工程师', '系统管理员', 'approved', '技术部内部晋升'),
(2, 'TR20260108002', 5, '赵六', 'EMP0004', 'internal', '财务部', '财务部', '会计', '主管会计', '财务组', '财务组', 'cadre', 'cadre', 7000.00, 8500.00, '2026-01-08', '业务熟练，晋升为主管', '系统管理员', 'approved', '财务部内部调整'),
(3, 'TR20260110003', 8, '周九', 'EMP0007', 'internal', '生产车间', '生产车间', '技术工人', '班长', '装配二组', '装配一组', 'worker', 'worker', 6500.00, 7200.00, '2026-01-10', '工作经验丰富，调整为班长', '系统管理员', 'pending', '待审批');

-- ============================================
-- 11. 公积金账户表
-- ============================================
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
(3, 'GJJ1705000003', '王五', '女', '人事部', 6500.00, 520.00, 520.00, '1992-03-10', 12480.00, 'active', '2022-08-10', 3),
(4, 'GJJ1705000004', '赵六', '男', '财务部', 7000.00, 560.00, 560.00, '1995-06-25', 13440.00, 'active', '2023-05-10', 5);

-- ============================================
-- 11. 公积金帐务表
-- ============================================
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
(7, 3, 'GJJ1705000003', '王五', '2022-09-01', 0.00, 1040.00, 1040.00, 'deposit', '月度缴存'),
(8, 4, 'GJJ1705000004', '赵六', '2023-05-10', 0.00, 0.00, 0.00, 'open', '开户'),
(9, 4, 'GJJ1705000004', '赵六', '2023-06-01', 0.00, 1120.00, 1120.00, 'deposit', '月度缴存');

-- ============================================
-- 完成提示
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT CONCAT('部门数: ', COUNT(*)) AS info FROM department;
SELECT CONCAT('员工数: ', COUNT(*)) AS info FROM employee;
SELECT CONCAT('调动记录数: ', COUNT(*)) AS info FROM transfer_record;
SELECT CONCAT('管理员账号: admin / admin') AS login_info;
