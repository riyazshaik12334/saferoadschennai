-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 28, 2026 at 10:03 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `saferoads_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `api_complaint`
--

CREATE TABLE `api_complaint` (
  `report_id` varchar(50) NOT NULL,
  `title` varchar(255) NOT NULL,
  `zone` varchar(100) NOT NULL,
  `reporter` varchar(100) NOT NULL,
  `status` varchar(50) NOT NULL,
  `date` varchar(100) NOT NULL,
  `priority` varchar(20) NOT NULL,
  `image_res_id` int(11) NOT NULL,
  `image` varchar(100) DEFAULT NULL,
  `description` longtext DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `reporter_mobile` varchar(20) NOT NULL,
  `reporter_email` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `supervisor_image` varchar(100) DEFAULT NULL,
  `supervisor_image_res_id` int(11) NOT NULL,
  `supervisor_name` varchar(150) DEFAULT NULL,
  `proof` varchar(100) DEFAULT NULL,
  `supervisor_updated_at` datetime(6) DEFAULT NULL,
  `authority_name` varchar(150) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `rating_description` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `api_complaint`
--

INSERT INTO `api_complaint` (`report_id`, `title`, `zone`, `reporter`, `status`, `date`, `priority`, `image_res_id`, `image`, `description`, `latitude`, `longitude`, `reporter_mobile`, `reporter_email`, `created_at`, `supervisor_image`, `supervisor_image_res_id`, `supervisor_name`, `proof`, `supervisor_updated_at`, `authority_name`, `rating`, `rating_description`) VALUES
('SRC-30154', '22G8+HJV, Kuthambakkam, Tamil Nadu 602105, India', 'Kuthambakkam', 'riyaz', 'Pending', '27 Mar 2026, 13:01', 'High', 0, 'complaints/UPLOAD_SRC-30154.jpg', 'damaged', 13.0262786, 80.0162329, '918074234992', 'riyazshaik.lg2@gmail.com', '2026-03-27 07:32:03.658554', '', 0, NULL, '', NULL, NULL, NULL, NULL),
('SRC-31400', '3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India', 'Kuthambakkam', 'riyaz', 'Resolved', '26 Mar 2026, 13:14', 'High', 0, 'complaints/UPLOAD_SRC-31400.jpg', '', 13.027027027027026, 80.02346017167355, '9347678977', 'riyazshaik.lg2@gmail.com', '2026-03-26 07:44:08.052554', 'supervisor_evidence/gallery_image_1774511956487.jpg', 0, 'suresh', 'proof_evidence/gallery_image_1774511956487.jpg', '2026-03-26 07:59:28.459862', 'RAJU', 5, 'nice'),
('SRC-31430', 'gg', 'Mylapore', 'riyaz', 'Resolved', '27 Mar 2026, 13:14', 'High', 0, 'complaints/WhatsApp_Image_2026-03-24_at_9.01.54_PM.jpeg', 'hi', 0, 0, '9347678977', 'riyazshaik.lg2@gmail.com', '2026-03-27 07:44:30.360162', 'supervisor_evidence/Screenshot_250.png', 0, 'suresh', 'proof_evidence/Screenshot_250.png', '2026-03-27 07:45:48.510484', NULL, 4, 'hi');

-- --------------------------------------------------------

--
-- Table structure for table `api_notification`
--

CREATE TABLE `api_notification` (
  `id` bigint(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `message` longtext NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `is_read` tinyint(1) NOT NULL,
  `type` varchar(20) NOT NULL,
  `user_email` varchar(254) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `api_notification`
--

INSERT INTO `api_notification` (`id`, `title`, `message`, `timestamp`, `is_read`, `type`, `user_email`) VALUES
(1, 'New Complaint Reported', 'A new complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been reported in Kuthambakkam.', '2026-03-25 11:28:41.303351', 0, 'AUTHORITY', NULL),
(2, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-25 11:30:44.523952', 0, 'SUPERVISOR', NULL),
(3, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 04:39:14.418187', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(4, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 04:39:17.670641', 0, 'SUPERVISOR', NULL),
(5, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 04:49:14.196867', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(6, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 04:49:17.119554', 0, 'SUPERVISOR', NULL),
(7, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 04:49:23.539493', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(8, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 04:50:01.449585', 0, 'SUPERVISOR', NULL),
(9, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 04:51:42.257405', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(10, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 05:01:03.786910', 0, 'SUPERVISOR', NULL),
(11, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 05:01:06.051931', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(12, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:01:08.298135', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(13, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 05:11:13.213031', 0, 'SUPERVISOR', NULL),
(14, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 05:11:15.106802', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(15, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 05:11:18.588203', 0, 'SUPERVISOR', NULL),
(16, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:11:20.546706', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(17, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 05:17:35.761874', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(18, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:17:38.686983', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(19, 'Action Required: In Progress', 'Authority has marked \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' in Kuthambakkam as In Progress. Please investigate and submit evidence.', '2026-03-26 05:17:51.354341', 0, 'SUPERVISOR', NULL),
(20, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:17:53.505413', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(21, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 05:25:37.568418', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(22, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:25:39.313292', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(23, 'Complaint Status Updated', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' is now \'Pending\'.', '2026-03-26 05:39:36.165428', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(24, 'Complaint Resolved', 'Your complaint \'22G4+PWF, Kuthambakkam, Tamil Nadu 602105, India\' has been successfully resolved.', '2026-03-26 05:39:38.993237', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(25, 'New Complaint Reported', 'A new complaint \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\' has been reported in Kuthambakkam.', '2026-03-26 07:44:08.086600', 0, 'AUTHORITY', NULL),
(26, 'Action Required: Processing Started', 'Authority has marked \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\' in Kuthambakkam as Processing. Please investigate and submit evidence.', '2026-03-26 07:51:32.230346', 0, 'SUPERVISOR', NULL),
(27, 'Evidence Submitted', 'Supervisor suresh has submitted evidence for \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\' in Kuthambakkam.', '2026-03-26 07:58:43.136873', 0, 'AUTHORITY', NULL),
(28, 'Evidence Submitted', 'Supervisor suresh has submitted evidence for \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\' in Kuthambakkam.', '2026-03-26 07:59:28.468046', 0, 'AUTHORITY', NULL),
(29, 'Complaint Resolved', 'Your complaint \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\' has been successfully resolved.', '2026-03-26 08:01:08.154658', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(30, 'Citizen Feedback Received', 'A citizen has submitted a 5-star rating for complaint \'3/47,Chettipedu Village, Kuthambakkam, Tamil Nadu 600123, India\'.', '2026-03-26 08:01:19.862128', 0, 'AUTHORITY', NULL),
(31, 'New Complaint Reported', 'A new complaint \'22G8+HJV, Kuthambakkam, Tamil Nadu 602105, India\' has been reported in Kuthambakkam.', '2026-03-27 07:32:03.706332', 0, 'AUTHORITY', NULL),
(32, 'New Complaint Reported', 'A new complaint \'gg\' has been reported in Mylapore.', '2026-03-27 07:44:30.392839', 0, 'AUTHORITY', NULL),
(33, 'Action Required: Processing Started', 'Authority has marked \'gg\' in Mylapore as Processing. Please investigate and submit evidence.', '2026-03-27 07:45:06.188907', 0, 'SUPERVISOR', NULL),
(34, 'Evidence Submitted', 'Supervisor suresh has submitted evidence for \'gg\' in Mylapore.', '2026-03-27 07:45:48.513791', 0, 'AUTHORITY', NULL),
(35, 'Complaint Resolved', 'Your complaint \'gg\' has been successfully resolved.', '2026-03-27 07:46:00.996458', 0, 'CITIZEN', 'riyazshaik.lg2@gmail.com'),
(36, 'Citizen Feedback Received', 'A citizen has submitted a 4-star rating for complaint \'gg\'.', '2026-03-27 07:46:32.383380', 0, 'AUTHORITY', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `api_otp`
--

CREATE TABLE `api_otp` (
  `id` bigint(20) NOT NULL,
  `email` varchar(254) NOT NULL,
  `code` varchar(4) NOT NULL,
  `created_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `api_otp`
--

INSERT INTO `api_otp` (`id`, `email`, `code`, `created_at`) VALUES
(1, 'riyazshaik.lg2@gmail.com', '5727', '2026-03-26 08:03:09.378955'),
(2, 'riyazshaik.lg2@gmail.com', '5044', '2026-03-26 08:03:42.898466'),
(3, 'riyazshaik.lg2@gmail.com', '6832', '2026-03-26 08:04:13.196691'),
(4, 'riyazshaik.lg2@gmail.com', '2114', '2026-03-26 08:19:31.183095'),
(5, 'riyazshaik.lg2@gmail.com', '8718', '2026-03-26 08:19:45.646438');

-- --------------------------------------------------------

--
-- Table structure for table `auth_group`
--

CREATE TABLE `auth_group` (
  `id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `auth_group_permissions`
--

CREATE TABLE `auth_group_permissions` (
  `id` bigint(20) NOT NULL,
  `group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `auth_permission`
--

CREATE TABLE `auth_permission` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `codename` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `auth_permission`
--

INSERT INTO `auth_permission` (`id`, `name`, `content_type_id`, `codename`) VALUES
(1, 'Can add log entry', 1, 'add_logentry'),
(2, 'Can change log entry', 1, 'change_logentry'),
(3, 'Can delete log entry', 1, 'delete_logentry'),
(4, 'Can view log entry', 1, 'view_logentry'),
(5, 'Can add permission', 3, 'add_permission'),
(6, 'Can change permission', 3, 'change_permission'),
(7, 'Can delete permission', 3, 'delete_permission'),
(8, 'Can view permission', 3, 'view_permission'),
(9, 'Can add group', 2, 'add_group'),
(10, 'Can change group', 2, 'change_group'),
(11, 'Can delete group', 2, 'delete_group'),
(12, 'Can view group', 2, 'view_group'),
(13, 'Can add user', 4, 'add_user'),
(14, 'Can change user', 4, 'change_user'),
(15, 'Can delete user', 4, 'delete_user'),
(16, 'Can view user', 4, 'view_user'),
(17, 'Can add content type', 5, 'add_contenttype'),
(18, 'Can change content type', 5, 'change_contenttype'),
(19, 'Can delete content type', 5, 'delete_contenttype'),
(20, 'Can view content type', 5, 'view_contenttype'),
(21, 'Can add session', 6, 'add_session'),
(22, 'Can change session', 6, 'change_session'),
(23, 'Can delete session', 6, 'delete_session'),
(24, 'Can view session', 6, 'view_session'),
(25, 'Can add complaint', 7, 'add_complaint'),
(26, 'Can change complaint', 7, 'change_complaint'),
(27, 'Can delete complaint', 7, 'delete_complaint'),
(28, 'Can view complaint', 7, 'view_complaint'),
(29, 'Can add notification', 8, 'add_notification'),
(30, 'Can change notification', 8, 'change_notification'),
(31, 'Can delete notification', 8, 'delete_notification'),
(32, 'Can view notification', 8, 'view_notification'),
(33, 'Can add otp', 9, 'add_otp'),
(34, 'Can change otp', 9, 'change_otp'),
(35, 'Can delete otp', 9, 'delete_otp'),
(36, 'Can view otp', 9, 'view_otp');

-- --------------------------------------------------------

--
-- Table structure for table `auth_user`
--

CREATE TABLE `auth_user` (
  `id` int(11) NOT NULL,
  `password` varchar(128) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `username` varchar(150) NOT NULL,
  `first_name` varchar(150) NOT NULL,
  `last_name` varchar(150) NOT NULL,
  `email` varchar(254) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `date_joined` datetime(6) NOT NULL,
  `is_authority` tinyint(1) DEFAULT 0,
  `is_supervisor` tinyint(1) DEFAULT 0,
  `role_id` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `auth_user`
--

INSERT INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `first_name`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `is_authority`, `is_supervisor`, `role_id`) VALUES
(6, '12345678', NULL, 1, 'riyaz', '', '', 'riyazshaik.lg2@gmail.com', 0, 1, '2026-03-25 10:57:44.936597', 0, 0, 1),
(7, 'password123', NULL, 0, 'RAJU', '', '', 'EMP12345', 1, 1, '2026-03-25 11:01:47.683751', 1, 0, 2),
(8, 'password123', NULL, 0, 'Employee_Two', '', '', 'EMP25241', 1, 1, '2026-03-25 11:01:47.721341', 1, 0, 2),
(9, 'password123', NULL, 0, 'suresh', '', '', 'SUP12345', 1, 1, '2026-03-25 11:01:47.731637', 0, 1, 3),
(10, 'password123', NULL, 0, 'Ramesh', '', '', 'SUP25241', 1, 1, '2026-03-25 11:01:47.741168', 0, 1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `auth_user_groups`
--

CREATE TABLE `auth_user_groups` (
  `id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `auth_user_user_permissions`
--

CREATE TABLE `auth_user_user_permissions` (
  `id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `django_admin_log`
--

CREATE TABLE `django_admin_log` (
  `id` int(11) NOT NULL,
  `action_time` datetime(6) NOT NULL,
  `object_id` longtext DEFAULT NULL,
  `object_repr` varchar(200) NOT NULL,
  `action_flag` smallint(5) UNSIGNED NOT NULL CHECK (`action_flag` >= 0),
  `change_message` longtext NOT NULL,
  `content_type_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `django_content_type`
--

CREATE TABLE `django_content_type` (
  `id` int(11) NOT NULL,
  `app_label` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `django_content_type`
--

INSERT INTO `django_content_type` (`id`, `app_label`, `model`) VALUES
(1, 'admin', 'logentry'),
(7, 'api', 'complaint'),
(8, 'api', 'notification'),
(9, 'api', 'otp'),
(2, 'auth', 'group'),
(3, 'auth', 'permission'),
(4, 'auth', 'user'),
(5, 'contenttypes', 'contenttype'),
(6, 'sessions', 'session');

-- --------------------------------------------------------

--
-- Table structure for table `django_migrations`
--

CREATE TABLE `django_migrations` (
  `id` bigint(20) NOT NULL,
  `app` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `django_migrations`
--

INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES
(1, 'contenttypes', '0001_initial', '2026-03-25 10:52:16.523591'),
(2, 'auth', '0001_initial', '2026-03-25 10:52:17.478205'),
(3, 'admin', '0001_initial', '2026-03-25 10:52:17.792615'),
(4, 'admin', '0002_logentry_remove_auto_add', '2026-03-25 10:52:17.814070'),
(5, 'admin', '0003_logentry_add_action_flag_choices', '2026-03-25 10:52:17.833518'),
(6, 'api', '0001_initial', '2026-03-25 10:52:17.955499'),
(7, 'contenttypes', '0002_remove_content_type_name', '2026-03-25 10:52:18.146000'),
(8, 'auth', '0002_alter_permission_name_max_length', '2026-03-25 10:52:18.286082'),
(9, 'auth', '0003_alter_user_email_max_length', '2026-03-25 10:52:18.385703'),
(10, 'auth', '0004_alter_user_username_opts', '2026-03-25 10:52:18.398968'),
(11, 'auth', '0005_alter_user_last_login_null', '2026-03-25 10:52:18.475216'),
(12, 'auth', '0006_require_contenttypes_0002', '2026-03-25 10:52:18.482347'),
(13, 'auth', '0007_alter_validators_add_error_messages', '2026-03-25 10:52:18.498643'),
(14, 'auth', '0008_alter_user_username_max_length', '2026-03-25 10:52:18.533052'),
(15, 'auth', '0009_alter_user_last_name_max_length', '2026-03-25 10:52:18.563804'),
(16, 'auth', '0010_alter_group_name_max_length', '2026-03-25 10:52:18.762532'),
(17, 'auth', '0011_update_proxy_permissions', '2026-03-25 10:52:18.805085'),
(18, 'auth', '0012_alter_user_first_name_max_length', '2026-03-25 10:52:18.852274'),
(19, 'sessions', '0001_initial', '2026-03-25 10:52:18.935616'),
(20, 'api', '0002_complaint_supervisor_image_and_more', '2026-03-25 11:18:23.402261'),
(21, 'api', '0003_complaint_proof', '2026-03-26 03:40:55.219099'),
(22, 'api', '0004_complaint_supervisor_updated_at', '2026-03-26 05:28:28.696751'),
(23, 'api', '0005_complaint_authority_name_complaint_rating_and_more', '2026-03-26 05:49:44.636370');

-- --------------------------------------------------------

--
-- Table structure for table `django_session`
--

CREATE TABLE `django_session` (
  `session_key` varchar(40) NOT NULL,
  `session_data` longtext NOT NULL,
  `expire_date` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `api_complaint`
--
ALTER TABLE `api_complaint`
  ADD PRIMARY KEY (`report_id`);

--
-- Indexes for table `api_notification`
--
ALTER TABLE `api_notification`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `api_otp`
--
ALTER TABLE `api_otp`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `auth_group`
--
ALTER TABLE `auth_group`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `auth_group_permissions`
--
ALTER TABLE `auth_group_permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `auth_group_permissions_group_id_permission_id_0cd325b0_uniq` (`group_id`,`permission_id`),
  ADD KEY `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` (`permission_id`);

--
-- Indexes for table `auth_permission`
--
ALTER TABLE `auth_permission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `auth_permission_content_type_id_codename_01ab375a_uniq` (`content_type_id`,`codename`);

--
-- Indexes for table `auth_user`
--
ALTER TABLE `auth_user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `auth_user_groups`
--
ALTER TABLE `auth_user_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `auth_user_groups_user_id_group_id_94350c0c_uniq` (`user_id`,`group_id`),
  ADD KEY `auth_user_groups_group_id_97559544_fk_auth_group_id` (`group_id`);

--
-- Indexes for table `auth_user_user_permissions`
--
ALTER TABLE `auth_user_user_permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `auth_user_user_permissions_user_id_permission_id_14a6b632_uniq` (`user_id`,`permission_id`),
  ADD KEY `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` (`permission_id`);

--
-- Indexes for table `django_admin_log`
--
ALTER TABLE `django_admin_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `django_admin_log_content_type_id_c4bce8eb_fk_django_co` (`content_type_id`),
  ADD KEY `django_admin_log_user_id_c564eba6_fk_auth_user_id` (`user_id`);

--
-- Indexes for table `django_content_type`
--
ALTER TABLE `django_content_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `django_content_type_app_label_model_76bd3d3b_uniq` (`app_label`,`model`);

--
-- Indexes for table `django_migrations`
--
ALTER TABLE `django_migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `django_session`
--
ALTER TABLE `django_session`
  ADD PRIMARY KEY (`session_key`),
  ADD KEY `django_session_expire_date_a5c62663` (`expire_date`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `api_notification`
--
ALTER TABLE `api_notification`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `api_otp`
--
ALTER TABLE `api_otp`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `auth_group`
--
ALTER TABLE `auth_group`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `auth_group_permissions`
--
ALTER TABLE `auth_group_permissions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `auth_permission`
--
ALTER TABLE `auth_permission`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `auth_user`
--
ALTER TABLE `auth_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `auth_user_groups`
--
ALTER TABLE `auth_user_groups`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `auth_user_user_permissions`
--
ALTER TABLE `auth_user_user_permissions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `django_admin_log`
--
ALTER TABLE `django_admin_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `django_content_type`
--
ALTER TABLE `django_content_type`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `django_migrations`
--
ALTER TABLE `django_migrations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `auth_group_permissions`
--
ALTER TABLE `auth_group_permissions`
  ADD CONSTRAINT `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  ADD CONSTRAINT `auth_group_permissions_group_id_b120cbf9_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`);

--
-- Constraints for table `auth_permission`
--
ALTER TABLE `auth_permission`
  ADD CONSTRAINT `auth_permission_content_type_id_2f476e4b_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`);

--
-- Constraints for table `auth_user_groups`
--
ALTER TABLE `auth_user_groups`
  ADD CONSTRAINT `auth_user_groups_group_id_97559544_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`),
  ADD CONSTRAINT `auth_user_groups_user_id_6a12ed8b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`);

--
-- Constraints for table `auth_user_user_permissions`
--
ALTER TABLE `auth_user_user_permissions`
  ADD CONSTRAINT `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  ADD CONSTRAINT `auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`);

--
-- Constraints for table `django_admin_log`
--
ALTER TABLE `django_admin_log`
  ADD CONSTRAINT `django_admin_log_content_type_id_c4bce8eb_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`),
  ADD CONSTRAINT `django_admin_log_user_id_c564eba6_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
