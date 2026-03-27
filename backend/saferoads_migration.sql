-- MySQL Dump converted from SQLite
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

-- Table structure for table `django_migrations`
CREATE TABLE IF NOT EXISTS `django_migrations` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `app` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `applied` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `django_migrations`
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (1, 'contenttypes', '0001_initial', '2026-02-26 08:52:05.152693');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (2, 'auth', '0001_initial', '2026-02-26 08:52:05.223992');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (3, 'admin', '0001_initial', '2026-02-26 08:52:05.268005');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (4, 'admin', '0002_logentry_remove_auto_add', '2026-02-26 08:52:05.303260');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (5, 'admin', '0003_logentry_add_action_flag_choices', '2026-02-26 08:52:05.329616');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (6, 'api', '0001_initial', '2026-02-26 08:52:05.349152');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (7, 'contenttypes', '0002_remove_content_type_name', '2026-02-26 08:52:05.386320');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (8, 'auth', '0002_alter_permission_name_max_length', '2026-02-26 08:52:05.416863');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (9, 'auth', '0003_alter_user_email_max_length', '2026-02-26 08:52:05.442814');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (10, 'auth', '0004_alter_user_username_opts', '2026-02-26 08:52:05.466559');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (11, 'auth', '0005_alter_user_last_login_null', '2026-02-26 08:52:05.491304');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (12, 'auth', '0006_require_contenttypes_0002', '2026-02-26 08:52:05.502921');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (13, 'auth', '0007_alter_validators_add_error_messages', '2026-02-26 08:52:05.525851');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (14, 'auth', '0008_alter_user_username_max_length', '2026-02-26 08:52:05.550591');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (15, 'auth', '0009_alter_user_last_name_max_length', '2026-02-26 08:52:05.574653');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (16, 'auth', '0010_alter_group_name_max_length', '2026-02-26 08:52:05.597533');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (17, 'auth', '0011_update_proxy_permissions', '2026-02-26 08:52:05.619594');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (18, 'auth', '0012_alter_user_first_name_max_length', '2026-02-26 08:52:05.645772');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (19, 'sessions', '0001_initial', '2026-02-26 08:52:05.670905');
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES (20, 'api', '0002_remove_complaint_id_remove_complaint_image_uri_and_more', '2026-03-02 07:45:56.147438');

-- Table structure for table `auth_group_permissions`
CREATE TABLE IF NOT EXISTS `auth_group_permissions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `group_id` INT(11) NOT NULL,
  `permission_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `auth_user_groups`
CREATE TABLE IF NOT EXISTS `auth_user_groups` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `group_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `auth_user_user_permissions`
CREATE TABLE IF NOT EXISTS `auth_user_user_permissions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `permission_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `django_admin_log`
CREATE TABLE IF NOT EXISTS `django_admin_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `object_id` TEXT,
  `object_repr` VARCHAR(255) NOT NULL,
  `action_flag` INT(11) NOT NULL,
  `change_message` TEXT NOT NULL,
  `content_type_id` INT(11),
  `user_id` INT(11) NOT NULL,
  `action_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `django_content_type`
CREATE TABLE IF NOT EXISTS `django_content_type` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `app_label` VARCHAR(255) NOT NULL,
  `model` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `django_content_type`
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (1, 'admin', 'logentry');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (2, 'auth', 'group');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (3, 'auth', 'permission');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (4, 'auth', 'user');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (5, 'contenttypes', 'contenttype');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (6, 'sessions', 'session');
INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES (7, 'api', 'complaint');

-- Table structure for table `auth_permission`
CREATE TABLE IF NOT EXISTS `auth_permission` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `content_type_id` INT(11) NOT NULL,
  `codename` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `auth_permission`
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (1, 1, 'add_logentry', 'Can add log entry');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (2, 1, 'change_logentry', 'Can change log entry');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (3, 1, 'delete_logentry', 'Can delete log entry');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (4, 1, 'view_logentry', 'Can view log entry');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (5, 3, 'add_permission', 'Can add permission');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (6, 3, 'change_permission', 'Can change permission');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (7, 3, 'delete_permission', 'Can delete permission');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (8, 3, 'view_permission', 'Can view permission');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (9, 2, 'add_group', 'Can add group');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (10, 2, 'change_group', 'Can change group');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (11, 2, 'delete_group', 'Can delete group');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (12, 2, 'view_group', 'Can view group');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (13, 4, 'add_user', 'Can add user');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (14, 4, 'change_user', 'Can change user');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (15, 4, 'delete_user', 'Can delete user');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (16, 4, 'view_user', 'Can view user');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (17, 5, 'add_contenttype', 'Can add content type');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (18, 5, 'change_contenttype', 'Can change content type');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (19, 5, 'delete_contenttype', 'Can delete content type');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (20, 5, 'view_contenttype', 'Can view content type');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (21, 6, 'add_session', 'Can add session');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (22, 6, 'change_session', 'Can change session');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (23, 6, 'delete_session', 'Can delete session');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (24, 6, 'view_session', 'Can view session');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (25, 7, 'add_complaint', 'Can add complaint');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (26, 7, 'change_complaint', 'Can change complaint');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (27, 7, 'delete_complaint', 'Can delete complaint');
INSERT INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES (28, 7, 'view_complaint', 'Can view complaint');

-- Table structure for table `auth_group`
CREATE TABLE IF NOT EXISTS `auth_group` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `auth_user`
CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(255) NOT NULL,
  `last_login` DATETIME,
  `is_superuser` BOOL NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `is_staff` BOOL NOT NULL,
  `is_active` BOOL NOT NULL,
  `date_joined` DATETIME NOT NULL,
  `first_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `auth_user`
INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES (1, 'pbkdf2_sha256$1200000$tKXjLEPMORGvcV3597L96u$hR6oV4TvAeS3sCQDZb0C456CgZ8SgW5Z5+M6vX/FoFU=', NULL, 1, 'admin', '', 'admin@example.com', 1, 1, '2026-02-26 08:52:07.052108', '');
INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES (2, 'pbkdf2_sha256$1200000$wO2bJsrCIpXcUInfbs5LU9$hp0eo6LriidGYthYzeRRzl5nBOElmgByq1ercAsec6M=', NULL, 0, 'riyazshaik252411@gmail.com', '', 'riyazshaik252411@gmail.com', 0, 1, '2026-02-26 09:20:37.659147', 'Riyaz Shaik');
INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES (3, 'pbkdf2_sha256$1200000$qJbVvB1yirOA367uQHXefi$AtWBcr7cRTsVQnKxeEwvYh5qHlnxdrpObc10JHs8FM4=', NULL, 0, 'authority_01', '', 'auth@src.gov.in', 0, 1, '2026-02-26 09:38:54.145486', 'Officer Ramesh');
INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES (4, 'pbkdf2_sha256$1200000$BjgK6MKcZxJPFvp0KBuJch$heptCLmPTYPhYjTB3d3jYJAR13d+dmyHfSycp3ahwyU=', NULL, 0, 'EMP12345', '', '', 0, 1, '2026-03-02 07:09:24.182237', 'Authority Officer');
INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES (5, 'pbkdf2_sha256$1200000$QNd3a2SbAeDI3VjAx5bHhm$b4Xuk/kV0zM5XXLDrSS7xPi8K6u9Q/OhCc2H5N3WvdY=', NULL, 0, 'riyazpspk22222@gmail.com', '', 'riyazpspk22222@gmail.com', 0, 1, '2026-03-02 07:36:46.023781', 'Riyazgigigi');

-- Table structure for table `django_session`
CREATE TABLE IF NOT EXISTS `django_session` (
  `session_key` VARCHAR(255) NOT NULL,
  `session_data` TEXT NOT NULL,
  `expire_date` DATETIME NOT NULL,
  PRIMARY KEY (`session_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `api_complaint`
CREATE TABLE IF NOT EXISTS `api_complaint` (
  `title` VARCHAR(255) NOT NULL,
  `zone` VARCHAR(255) NOT NULL,
  `reporter` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `date` VARCHAR(255) NOT NULL,
  `priority` VARCHAR(255) NOT NULL,
  `image_res_id` INT(11) NOT NULL,
  `description` TEXT,
  `latitude` REAL NOT NULL,
  `longitude` REAL NOT NULL,
  `reporter_mobile` VARCHAR(255) NOT NULL,
  `image` VARCHAR(255),
  `report_id` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `api_complaint`
INSERT INTO `api_complaint` (`title`, `zone`, `reporter`, `status`, `date`, `priority`, `image_res_id`, `description`, `latitude`, `longitude`, `reporter_mobile`, `image`, `report_id`) VALUES ('Test Title', 'Test Zone', 'Test Reporter', 'Resolved', '2026-03-02', 'High', 0, 'Test Description', 13.0, 80.0, '1234567890', '', 'TEST-456');
INSERT INTO `api_complaint` (`title`, `zone`, `reporter`, `status`, `date`, `priority`, `image_res_id`, `description`, `latitude`, `longitude`, `reporter_mobile`, `image`, `report_id`) VALUES ('3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India', 'Kuthambakkam', 'Riyazgigigi', 'In Progress', '02 Mar 2026, 14:44', 'Medium', 0, '', 13.027027027027026, 80.02346017167355, '9347678977', '', 'SRC-44428');

