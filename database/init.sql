-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lab_equipment_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `borrow_record`
--

DROP TABLE IF EXISTS `borrow_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_no` varchar(30) NOT NULL COMMENT '借用编号',
  `user_id` bigint NOT NULL COMMENT '借用用户',
  `device_id` bigint NOT NULL COMMENT '借用设备',
  `reservation_id` bigint DEFAULT NULL COMMENT '关联预约，可为空',
  `borrow_time` datetime NOT NULL COMMENT '借出时间',
  `expected_return_time` datetime NOT NULL COMMENT '预计归还时间',
  `actual_return_time` datetime DEFAULT NULL COMMENT '实际归还时间',
  `status` varchar(20) NOT NULL DEFAULT 'BORROWED' COMMENT 'BORROWED借用中，RETURNED已归还，OVERDUE已逾期',
  `remark` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active_device_id` bigint GENERATED ALWAYS AS ((case when (`status` in (_utf8mb4'BORROWED',_utf8mb4'OVERDUE')) then `device_id` else NULL end)) STORED,
  PRIMARY KEY (`id`),
  UNIQUE KEY `borrow_no` (`borrow_no`),
  UNIQUE KEY `uq_borrow_active_device` (`active_device_id`),
  KEY `fk_borrow_user` (`user_id`),
  KEY `fk_borrow_reservation` (`reservation_id`),
  KEY `idx_borrow_device_status` (`device_id`,`status`),
  CONSTRAINT `fk_borrow_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `fk_borrow_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`),
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_borrow_status_return` CHECK ((((`status` in (_utf8mb4'BORROWED',_utf8mb4'OVERDUE')) and (`actual_return_time` is null)) or ((`status` = _utf8mb4'RETURNED') and (`actual_return_time` is not null) and (`actual_return_time` >= `borrow_time`)))),
  CONSTRAINT `chk_expected_return_time` CHECK ((`expected_return_time` > `borrow_time`))
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备借还记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_record`
--

/*!40000 ALTER TABLE `borrow_record` DISABLE KEYS */;
INSERT INTO `borrow_record` (`id`, `borrow_no`, `user_id`, `device_id`, `reservation_id`, `borrow_time`, `expected_return_time`, `actual_return_time`, `status`, `remark`, `created_at`) VALUES (1,'BOR202608001',1,1,NULL,'2026-08-10 09:00:00','2026-08-10 11:00:00','2026-08-10 10:45:00','RETURNED',NULL,'2026-08-31 20:57:32'),(2,'BOR202608002',2,2,NULL,'2026-08-11 14:00:00','2026-08-11 16:00:00','2026-08-11 15:50:00','RETURNED',NULL,'2026-08-31 20:57:35'),(3,'BOR202608003',3,3,NULL,'2026-08-12 09:00:00','2026-08-12 11:00:00','2026-08-12 10:40:00','RETURNED',NULL,'2026-08-31 20:57:41'),(4,'BOR202608004',4,4,NULL,'2026-08-13 13:00:00','2026-08-13 16:00:00','2026-08-13 15:55:00','RETURNED',NULL,'2026-08-31 20:57:44'),(5,'BOR202608005',5,5,NULL,'2026-08-14 09:00:00','2026-08-14 12:00:00','2026-08-14 11:45:00','RETURNED',NULL,'2026-08-31 20:57:46'),(6,'BOR202608006',6,6,NULL,'2026-08-15 10:00:00','2026-08-15 12:00:00','2026-08-15 11:50:00','RETURNED',NULL,'2026-08-31 20:57:51'),(7,'BOR202608007',7,7,NULL,'2026-08-16 14:00:00','2026-08-16 16:00:00','2026-08-16 15:45:00','RETURNED',NULL,'2026-08-31 20:57:54'),(8,'BOR202608008',1,8,NULL,'2026-08-17 09:00:00','2026-08-17 12:00:00','2026-08-17 11:30:00','RETURNED',NULL,'2026-08-31 20:57:56'),(9,'BOR202608009',2,9,NULL,'2026-08-18 13:00:00','2026-08-18 15:00:00','2026-08-18 14:50:00','RETURNED',NULL,'2026-08-31 20:57:58'),(10,'BOR202608010',3,10,NULL,'2026-08-19 10:00:00','2026-08-19 12:00:00','2026-08-19 11:40:00','RETURNED',NULL,'2026-08-31 20:58:00'),(11,'BOR202608011',1,1,NULL,'2026-08-31 21:30:00','2026-09-01 09:00:00','2026-08-31 22:00:00','RETURNED',NULL,'2026-08-31 21:19:14'),(12,'BOR20260920210449818',8,8,NULL,'2026-09-20 21:04:49','2026-09-20 23:04:00','2026-09-20 21:05:24','RETURNED',NULL,'2026-09-20 21:04:49'),(13,'BOR20260921130641786',1,1,NULL,'2026-09-21 13:06:41','2026-09-21 15:06:00','2026-09-21 13:06:48','RETURNED',NULL,'2026-09-21 13:06:41'),(14,'BOR20260921133408913',2,4,NULL,'2026-09-21 13:34:08','2026-09-21 15:34:00','2026-09-21 13:34:31','RETURNED',NULL,'2026-09-21 13:34:08'),(15,'BOR20260922215200509',12,1,NULL,'2026-09-22 21:52:00','2026-09-22 23:51:00','2026-09-22 21:52:07','RETURNED',NULL,'2026-09-22 21:52:00'),(16,'BOR20260922215227768',12,8,NULL,'2026-09-22 21:52:27','2026-09-26 23:52:00',NULL,'BORROWED',NULL,'2026-09-22 21:52:27'),(17,'BOR20260922215419098',12,1,NULL,'2026-09-22 21:54:19','2026-09-22 23:54:00','2026-10-14 17:52:48','RETURNED',NULL,'2026-09-22 21:54:19'),(19,'BOR20261014175225255',12,2,NULL,'2026-10-14 17:52:25','2026-10-15 19:52:00',NULL,'BORROWED',NULL,'2026-10-14 17:52:25'),(20,'BOR20261014182932088',12,6,NULL,'2026-10-14 18:29:32','2026-10-14 22:29:00','2026-10-14 18:29:48','RETURNED',NULL,'2026-10-14 18:29:32'),(21,'BOR20261016122803906',1,4,NULL,'2026-10-16 12:28:03','2026-10-16 14:28:00','2026-10-16 12:29:03','RETURNED',NULL,'2026-10-16 12:28:03'),(22,'BOR20261016160607284',12,1,NULL,'2026-10-16 16:06:07','2026-10-16 18:05:00','2026-10-16 16:06:34','RETURNED',NULL,'2026-10-16 16:06:07');
/*!40000 ALTER TABLE `borrow_record` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_borrow_insert` AFTER INSERT ON `borrow_record` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '借还管理', '借用设备', 'borrow_record', NEW.id,
 CONCAT('借用记录：', NEW.borrow_no)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_borrow_after_insert` AFTER INSERT ON `borrow_record` FOR EACH ROW BEGIN
    IF NEW.status IN ('BORROWED', 'OVERDUE') THEN
        UPDATE device
        SET status = 'BORROWED'
        WHERE id = NEW.device_id
          AND status = 'AVAILABLE'
          AND NOT EXISTS (
              SELECT 1 FROM maintenance_record m
              WHERE m.device_id = NEW.device_id
                AND m.status IN ('PENDING', 'PROCESSING')
          );

        IF ROW_COUNT() <> 1 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '设备不可借用：已借出、待维修或已停用';
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_borrow_before_update` BEFORE UPDATE ON `borrow_record` FOR EACH ROW BEGIN
    IF NEW.device_id <> OLD.device_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '借用记录不能更换设备，请新建借用记录';
    END IF;

    IF OLD.status = 'RETURNED' AND NEW.status <> 'RETURNED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '已归还记录不能重新改为借用中';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_borrow_after_update` AFTER UPDATE ON `borrow_record` FOR EACH ROW BEGIN
    IF OLD.status IN ('BORROWED', 'OVERDUE')
       AND NEW.status = 'RETURNED' THEN
        UPDATE device
        SET status = CASE
            WHEN EXISTS (
                SELECT 1 FROM maintenance_record m
                WHERE m.device_id = NEW.device_id
                  AND m.status IN ('PENDING', 'PROCESSING')
            ) THEN 'MAINTENANCE'
            ELSE 'AVAILABLE'
        END
        WHERE id = NEW.device_id
          AND status = 'BORROWED';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_borrow_update` AFTER UPDATE ON `borrow_record` FOR EACH ROW BEGIN
    IF OLD.status IN ('BORROWED', 'OVERDUE')
       AND NEW.status = 'RETURNED' THEN
        INSERT INTO operation_log
            (user_id, module_name, action_name, target_type, target_id, detail)
        VALUES
            (@app_user_id, '借还管理', '归还设备', 'borrow_record', NEW.id,
             CONCAT('归还记录：', NEW.borrow_no,
                    '；借还状态：', OLD.status, ' → ', NEW.status));
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_no` varchar(30) NOT NULL COMMENT '设备编号',
  `device_name` varchar(100) NOT NULL COMMENT '设备名称',
  `category_id` bigint NOT NULL COMMENT '设备分类',
  `laboratory_id` bigint NOT NULL COMMENT '所属实验室',
  `brand` varchar(50) DEFAULT NULL,
  `model` varchar(100) DEFAULT NULL,
  `purchase_date` date DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE可用，BORROWED借出，MAINTENANCE维修中，DISABLED停用',
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_no` (`device_no`),
  KEY `fk_device_category` (`category_id`),
  KEY `fk_device_laboratory` (`laboratory_id`),
  CONSTRAINT `fk_device_category` FOREIGN KEY (`category_id`) REFERENCES `device_category` (`id`),
  CONSTRAINT `fk_device_laboratory` FOREIGN KEY (`laboratory_id`) REFERENCES `laboratory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,'DEV001','数字示波器',1,4,'RIGOL','DS1054Z','2024-03-15','MAINTENANCE',NULL,'2026-08-31 20:43:57'),(2,'DEV002','数字万用表',2,4,'FLUKE','17B+','2024-03-20','BORROWED',NULL,'2026-08-31 20:44:00'),(3,'DEV003','函数信号发生器',3,4,'RIGOL','DG1022Z','2023-10-12','AVAILABLE',NULL,'2026-08-31 20:44:02'),(4,'DEV004','Arduino开发板',4,7,'Arduino','UNO R3','2025-02-18','AVAILABLE',NULL,'2026-08-31 20:44:05'),(5,'DEV005','机架式服务器',5,9,'Dell','PowerEdge R750','2023-06-08','MAINTENANCE',NULL,'2026-08-31 20:44:18'),(6,'DEV006','企业级路由器',6,3,'Huawei','AR1220','2024-01-25','AVAILABLE',NULL,'2026-08-31 20:44:21'),(7,'DEV007','高清投影仪',7,1,'Epson','CB-X49','2024-09-03','AVAILABLE',NULL,'2026-08-31 20:44:24'),(8,'DEV008','3D打印机',8,2,'Creality','Ender-3 V3','2025-01-10','BORROWED',NULL,'2026-08-31 20:44:26'),(9,'DEV009','高清摄像机',9,10,'Logitech','C920','2024-05-22','AVAILABLE',NULL,'2026-08-31 20:44:29'),(10,'DEV010','GPU工作站',10,5,'Lenovo','ThinkStation P620','2025-03-01','AVAILABLE',NULL,'2026-08-31 20:44:33'),(12,'DEVTEST001','数字存储示波器',1,4,'普源精电','DS1054Z',NULL,'DISABLED',NULL,'2026-09-22 21:39:42');
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_device_insert` AFTER INSERT ON `device` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '设备管理', '新增', 'device', NEW.id,
 CONCAT('新增设备：', NEW.device_no, ' ', NEW.device_name)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_device_update` AFTER UPDATE ON `device` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '设备管理', '修改', 'device', NEW.id,
 CONCAT('更新设备状态：', OLD.status, ' → ', NEW.status)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_device_delete` AFTER DELETE ON `device` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '设备管理', '删除', 'device', OLD.id,
 CONCAT('删除设备：', OLD.device_no, ' ', OLD.device_name)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `device_category`
--

DROP TABLE IF EXISTS `device_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_code` varchar(30) NOT NULL,
  `category_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_code` (`category_code`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_category`
--

/*!40000 ALTER TABLE `device_category` DISABLE KEYS */;
INSERT INTO `device_category` VALUES (1,'OSC','示波器',NULL,'2026-08-31 20:42:01'),(2,'MULTI','万用表',NULL,'2026-08-31 20:42:05'),(3,'SIGNAL','信号发生器',NULL,'2026-08-31 20:42:08'),(4,'BOARD','开发板',NULL,'2026-08-31 20:42:15'),(5,'SERVER','服务器',NULL,'2026-08-31 20:42:18'),(6,'ROUTER','路由器',NULL,'2026-08-31 20:42:20'),(7,'PROJECTOR','投影仪',NULL,'2026-08-31 20:42:32'),(8,'PRINTER','3D打印机',NULL,'2026-08-31 20:42:34'),(9,'CAMERA','摄像机',NULL,'2026-08-31 20:42:37'),(10,'WORKSTATION','GPU工作站',NULL,'2026-08-31 20:42:38');
/*!40000 ALTER TABLE `device_category` ENABLE KEYS */;

--
-- Table structure for table `laboratory`
--

DROP TABLE IF EXISTS `laboratory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `laboratory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lab_code` varchar(30) NOT NULL COMMENT '实验室编号',
  `lab_name` varchar(100) NOT NULL COMMENT '实验室名称',
  `location` varchar(100) NOT NULL COMMENT '所在地点',
  `capacity` int NOT NULL COMMENT '可容纳人数',
  `status` varchar(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE可用，MAINTENANCE维护中，DISABLED停用',
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `lab_code` (`lab_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实验室信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `laboratory`
--

/*!40000 ALTER TABLE `laboratory` DISABLE KEYS */;
INSERT INTO `laboratory` VALUES (1,'LAB101','数据库实验室','信息楼A101',40,'AVAILABLE',NULL,'2026-08-31 20:40:14'),(2,'LAB102','软件工程实验室','信息楼A102',45,'AVAILABLE',NULL,'2026-08-31 20:40:38'),(3,'LAB103','网络安全实验室','信息楼A103',35,'AVAILABLE',NULL,'2026-08-31 20:40:43'),(4,'LAB104','嵌入式系统实验室','信息楼A104',30,'AVAILABLE',NULL,'2026-08-31 20:40:47'),(5,'LAB105','人工智能实验室','信息楼A105',50,'AVAILABLE',NULL,'2026-08-31 20:40:52'),(6,'LAB201','计算机组成原理实验室','信息楼B201',40,'AVAILABLE',NULL,'2026-08-31 20:41:13'),(7,'LAB202','物联网实验室','信息楼B202',35,'AVAILABLE',NULL,'2026-08-31 20:41:20'),(8,'LAB203','移动应用开发实验室','信息楼B203',45,'AVAILABLE',NULL,'2026-08-31 20:41:23'),(9,'LAB204','云计算实验室','信息楼B204',40,'MAINTENANCE',NULL,'2026-08-31 20:41:25'),(10,'LAB205','多媒体技术实验室','信息楼B205',50,'AVAILABLE',NULL,'2026-08-31 20:41:30');
/*!40000 ALTER TABLE `laboratory` ENABLE KEYS */;

--
-- Table structure for table `maintenance_record`
--

DROP TABLE IF EXISTS `maintenance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `maintenance_no` varchar(30) NOT NULL COMMENT '维修单号',
  `device_id` bigint NOT NULL COMMENT '维修设备',
  `reporter_user_id` bigint NOT NULL COMMENT '报修用户',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `maintenance_start_time` datetime DEFAULT NULL COMMENT '维修开始时间',
  `maintenance_end_time` datetime DEFAULT NULL COMMENT '维修结束时间',
  `fault_description` varchar(255) NOT NULL COMMENT '故障描述',
  `cost` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '维修费用',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING待处理，PROCESSING维修中，COMPLETED已完成',
  `remark` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `maintenance_no` (`maintenance_no`),
  KEY `fk_maintenance_reporter` (`reporter_user_id`),
  KEY `idx_maintenance_device_status` (`device_id`,`status`),
  CONSTRAINT `fk_maintenance_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `fk_maintenance_reporter` FOREIGN KEY (`reporter_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备维修记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_record`
--

/*!40000 ALTER TABLE `maintenance_record` DISABLE KEYS */;
INSERT INTO `maintenance_record` VALUES (1,'MNT202608001',1,1,'2026-08-01 09:00:00','2026-08-01 10:00:00','2026-08-01 14:00:00','探头接口接触不良',80.00,'COMPLETED',NULL,'2026-08-31 20:59:51'),(2,'MNT202608002',2,2,'2026-08-02 10:00:00','2026-08-02 11:00:00','2026-08-02 15:00:00','显示屏亮度异常',120.00,'COMPLETED',NULL,'2026-08-31 20:59:53'),(3,'MNT202608003',3,3,'2026-08-03 09:00:00','2026-08-03 10:00:00','2026-08-03 16:00:00','输出信号不稳定',150.00,'COMPLETED',NULL,'2026-08-31 20:59:54'),(4,'MNT202608004',4,4,'2026-08-04 13:00:00','2026-08-04 14:00:00','2026-08-04 17:00:00','USB接口松动',50.00,'COMPLETED',NULL,'2026-08-31 21:00:14'),(5,'MNT202608005',5,5,'2026-08-29 09:00:00','2026-08-29 10:00:00',NULL,'服务器磁盘阵列告警',0.00,'PROCESSING',NULL,'2026-08-31 21:00:15'),(6,'MNT202608006',6,6,'2026-08-06 10:00:00','2026-08-06 11:00:00','2026-08-06 15:00:00','网络端口损坏',100.00,'COMPLETED',NULL,'2026-08-31 21:00:19'),(7,'MNT202608007',7,7,'2026-08-07 14:00:00','2026-08-07 15:00:00','2026-08-07 17:00:00','投影画面偏色',180.00,'COMPLETED',NULL,'2026-08-31 21:00:21'),(8,'MNT202608008',8,1,'2026-08-08 09:00:00','2026-08-08 10:00:00','2026-08-08 18:00:00','打印喷头堵塞',200.00,'COMPLETED',NULL,'2026-08-31 21:00:30'),(9,'MNT202608009',9,2,'2026-08-09 13:00:00','2026-08-09 14:00:00','2026-08-09 16:00:00','摄像头无法对焦',90.00,'COMPLETED',NULL,'2026-08-31 21:00:33'),(10,'MNT202608010',10,3,'2026-08-10 11:00:00','2026-08-10 12:00:00','2026-08-10 16:00:00','显卡驱动异常',60.00,'COMPLETED',NULL,'2026-08-31 21:00:35'),(11,'MNT20260921133431275',4,2,'2026-09-21 13:34:31','2026-09-21 13:35:23','2026-09-21 13:36:10','不好使',20.50,'COMPLETED','已恢复','2026-09-21 13:34:31'),(12,'MNT20261014182948901',6,12,'2026-10-14 18:29:48','2026-10-14 18:30:00','2026-10-14 18:30:17','不好使',0.00,'COMPLETED','已恢复','2026-10-14 18:29:48'),(13,'MNT20261016122903277',4,1,'2026-10-16 12:29:03','2026-10-16 12:29:13','2026-10-16 12:29:29','设备损坏',22.00,'COMPLETED','已更换','2026-10-16 12:29:03'),(14,'MNT20261016160634125',1,12,'2026-10-16 16:06:34','2026-10-16 16:06:44',NULL,'不好使',0.00,'PROCESSING',NULL,'2026-10-16 16:06:34');
/*!40000 ALTER TABLE `maintenance_record` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_maintenance_insert` AFTER INSERT ON `maintenance_record` FOR EACH ROW BEGIN
    INSERT INTO operation_log
    (
        user_id,
        module_name,
        action_name,
        target_type,
        target_id,
        detail
    )
    VALUES
    (
        COALESCE(@app_user_id, NEW.reporter_user_id),
        '维修管理',
        '提交报修',
        'maintenance_record',
        NEW.id,
        CONCAT(
            '维修编号：', NEW.maintenance_no,
            '；故障说明：', NEW.fault_description
        )
    );
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_maintenance_update` AFTER UPDATE ON `maintenance_record` FOR EACH ROW BEGIN
    DECLARE v_action_name VARCHAR(50);

    IF NOT (OLD.status <=> NEW.status) THEN

        SET v_action_name =
            CASE NEW.status
                WHEN 'PROCESSING' THEN '开始维修'
                WHEN 'COMPLETED' THEN '完成维修'
                WHEN 'CANCELLED' THEN '取消维修'
                ELSE '维修状态修改'
            END;

        INSERT INTO operation_log
        (
            user_id,
            module_name,
            action_name,
            target_type,
            target_id,
            detail
        )
        VALUES
        (
            @app_user_id,
            '维修管理',
            v_action_name,
            'maintenance_record',
            NEW.id,
            CONCAT(
                '维修编号：', NEW.maintenance_no,
                '；维修状态：', OLD.status, ' → ', NEW.status,
                '；处理结果：', COALESCE(NEW.remark, '无'),
                '；维修费用：', COALESCE(NEW.cost, 0)
            )
        );

    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '应用当前登录用户',
  `module_name` varchar(50) NOT NULL COMMENT '功能模块',
  `action_name` varchar(50) NOT NULL COMMENT '操作类型',
  `target_type` varchar(50) NOT NULL COMMENT '目标表或对象',
  `target_id` bigint DEFAULT NULL COMMENT '目标记录编号',
  `detail` varchar(255) DEFAULT NULL,
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_time` (`operation_time`),
  KEY `idx_operation_log_user` (`user_id`),
  CONSTRAINT `fk_operation_log_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作审计日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,1,'预约管理','提交预约','reservation',15,'提交预约：RES20260901150208016','2026-09-01 15:02:08'),(2,8,'预约管理','提交预约','reservation',16,'提交预约：RES20260916201543723','2026-09-16 20:15:43'),(3,8,'预约管理','提交预约','reservation',17,'提交预约：RES20260916201551964','2026-09-16 20:15:51'),(4,8,'预约管理','状态修改','reservation',15,'预约状态：PENDING → APPROVED','2026-09-16 20:22:00'),(5,8,'预约管理','状态修改','reservation',16,'预约状态：PENDING → APPROVED','2026-09-16 20:22:19'),(6,8,'预约管理','状态修改','reservation',17,'预约状态：PENDING → APPROVED','2026-09-16 20:22:35'),(7,8,'设备管理','修改','device',8,'更新设备状态：AVAILABLE → BORROWED','2026-09-20 21:04:49'),(8,8,'借还管理','借用设备','borrow_record',12,'借用记录：BOR20260920210449818','2026-09-20 21:04:49'),(9,1,'设备管理','修改','device',8,'更新设备状态：BORROWED → AVAILABLE','2026-09-20 21:05:24'),(10,1,'借还管理','归还设备','borrow_record',12,'借还状态：BORROWED → RETURNED','2026-09-20 21:05:24'),(11,1,'预约管理','提交预约','reservation',18,'提交预约：RES20260920210635659','2026-09-20 21:06:35'),(12,8,'预约管理','状态修改','reservation',18,'预约状态：PENDING → REJECTED','2026-09-20 21:07:07'),(13,1,'设备管理','修改','device',1,'更新设备状态：AVAILABLE → BORROWED','2026-09-21 13:06:41'),(14,1,'借还管理','借用设备','borrow_record',13,'借用记录：BOR20260921130641786','2026-09-21 13:06:41'),(15,1,'设备管理','修改','device',1,'更新设备状态：BORROWED → AVAILABLE','2026-09-21 13:06:48'),(16,1,'借还管理','归还设备','borrow_record',13,'借还状态：BORROWED → RETURNED','2026-09-21 13:06:48'),(17,2,'设备管理','修改','device',4,'更新设备状态：AVAILABLE → BORROWED','2026-09-21 13:34:08'),(18,2,'借还管理','借用设备','borrow_record',14,'借用记录：BOR20260921133408913','2026-09-21 13:34:08'),(19,2,'设备管理','修改','device',4,'更新设备状态：BORROWED → AVAILABLE','2026-09-21 13:34:31'),(20,2,'借还管理','归还设备','borrow_record',14,'借还状态：BORROWED → RETURNED','2026-09-21 13:34:31'),(21,2,'设备管理','修改','device',4,'更新设备状态：AVAILABLE → MAINTENANCE','2026-09-21 13:34:31'),(22,11,'设备管理','修改','device',4,'更新设备状态：MAINTENANCE → AVAILABLE','2026-09-21 13:36:11'),(23,12,'设备管理','新增','device',12,'新增设备：DEVTEST001 数字存储示波器','2026-09-22 21:39:42'),(24,12,'设备管理','修改','device',12,'更新设备状态：AVAILABLE → DISABLED','2026-09-22 21:41:23'),(25,12,'预约管理','状态修改','reservation',8,'预约状态：PENDING → APPROVED','2026-09-22 21:49:15'),(26,12,'设备管理','修改','device',1,'更新设备状态：AVAILABLE → BORROWED','2026-09-22 21:52:00'),(27,12,'借还管理','借用设备','borrow_record',15,'借用记录：BOR20260922215200509','2026-09-22 21:52:00'),(28,12,'设备管理','修改','device',1,'更新设备状态：BORROWED → AVAILABLE','2026-09-22 21:52:07'),(29,12,'借还管理','归还设备','borrow_record',15,'归还记录：BOR20260922215200509；借还状态：BORROWED → RETURNED','2026-09-22 21:52:07'),(30,12,'设备管理','修改','device',8,'更新设备状态：AVAILABLE → BORROWED','2026-09-22 21:52:27'),(31,12,'借还管理','借用设备','borrow_record',16,'借用记录：BOR20260922215227768','2026-09-22 21:52:27'),(32,12,'设备管理','修改','device',1,'更新设备状态：AVAILABLE → BORROWED','2026-09-22 21:54:19'),(33,12,'借还管理','借用设备','borrow_record',17,'借用记录：BOR20260922215419098','2026-09-22 21:54:19'),(39,8,'预约管理','提交预约','reservation',19,'提交预约：RES20261014173830230','2026-10-14 17:38:30'),(40,12,'借还管理','借用设备','borrow_record',19,'借用记录：BOR20261014175225255','2026-10-14 17:52:25'),(41,12,'设备管理','修改','device',2,'更新设备状态：AVAILABLE → BORROWED','2026-10-14 17:52:25'),(42,12,'设备管理','修改','device',1,'更新设备状态：BORROWED → AVAILABLE','2026-10-14 17:52:48'),(43,12,'借还管理','归还设备','borrow_record',17,'归还记录：BOR20260922215419098；借还状态：BORROWED → RETURNED','2026-10-14 17:52:48'),(44,12,'借还管理','借用设备','borrow_record',20,'借用记录：BOR20261014182932088','2026-10-14 18:29:32'),(45,12,'设备管理','修改','device',6,'更新设备状态：AVAILABLE → BORROWED','2026-10-14 18:29:32'),(46,12,'设备管理','修改','device',6,'更新设备状态：BORROWED → AVAILABLE','2026-10-14 18:29:48'),(47,12,'借还管理','归还设备','borrow_record',20,'归还记录：BOR20261014182932088；借还状态：BORROWED → RETURNED','2026-10-14 18:29:48'),(48,12,'维修管理','提交报修','maintenance_record',12,'维修编号：MNT20261014182948901；故障说明：不好使','2026-10-14 18:29:48'),(49,12,'设备管理','修改','device',6,'更新设备状态：AVAILABLE → MAINTENANCE','2026-10-14 18:29:48'),(50,12,'维修管理','开始维修','maintenance_record',12,'维修编号：MNT20261014182948901；维修状态：PENDING → PROCESSING；处理结果：无；维修费用：0.00','2026-10-14 18:30:00'),(51,12,'维修管理','完成维修','maintenance_record',12,'维修编号：MNT20261014182948901；维修状态：PROCESSING → COMPLETED；处理结果：已恢复；维修费用：0.00','2026-10-14 18:30:17'),(52,12,'设备管理','修改','device',6,'更新设备状态：MAINTENANCE → AVAILABLE','2026-10-14 18:30:17'),(53,1,'预约管理','提交预约','reservation',20,'提交预约：RES20261015221338060','2026-10-15 22:13:38'),(54,12,'预约管理','状态修改','reservation',20,'预约状态：PENDING → APPROVED','2026-10-15 22:13:51'),(55,12,'预约管理','提交预约','reservation',21,'提交预约：RES20261016122424902','2026-10-16 12:24:24'),(56,12,'预约管理','提交预约','reservation',22,'提交预约：RES20261016122706462','2026-10-16 12:27:06'),(57,12,'预约管理','状态修改','reservation',21,'预约状态：PENDING → APPROVED','2026-10-16 12:27:36'),(58,1,'借还管理','借用设备','borrow_record',21,'借用记录：BOR20261016122803906','2026-10-16 12:28:03'),(59,1,'设备管理','修改','device',4,'更新设备状态：AVAILABLE → BORROWED','2026-10-16 12:28:03'),(60,1,'设备管理','修改','device',4,'更新设备状态：BORROWED → AVAILABLE','2026-10-16 12:29:03'),(61,1,'借还管理','归还设备','borrow_record',21,'归还记录：BOR20261016122803906；借还状态：BORROWED → RETURNED','2026-10-16 12:29:03'),(62,1,'维修管理','提交报修','maintenance_record',13,'维修编号：MNT20261016122903277；故障说明：设备损坏','2026-10-16 12:29:03'),(63,1,'设备管理','修改','device',4,'更新设备状态：AVAILABLE → MAINTENANCE','2026-10-16 12:29:03'),(64,12,'维修管理','开始维修','maintenance_record',13,'维修编号：MNT20261016122903277；维修状态：PENDING → PROCESSING；处理结果：无；维修费用：0.00','2026-10-16 12:29:13'),(65,12,'维修管理','完成维修','maintenance_record',13,'维修编号：MNT20261016122903277；维修状态：PROCESSING → COMPLETED；处理结果：已更换；维修费用：22.00','2026-10-16 12:29:29'),(66,12,'设备管理','修改','device',4,'更新设备状态：MAINTENANCE → AVAILABLE','2026-10-16 12:29:29'),(67,12,'设备管理','新增','device',14,'新增设备：DEV022 电子示波器','2026-10-16 15:29:31'),(68,12,'设备管理','删除','device',14,'删除设备：DEV022 电子示波器','2026-10-16 15:29:54'),(69,1,'预约管理','提交预约','reservation',23,'提交预约：RES20261016160329924','2026-10-16 16:03:29'),(70,12,'预约管理','提交预约','reservation',24,'提交预约：RES20261016160353968','2026-10-16 16:03:53'),(71,1,'预约管理','提交预约','reservation',25,'提交预约：RES20261016160439230','2026-10-16 16:04:39'),(72,12,'借还管理','借用设备','borrow_record',22,'借用记录：BOR20261016160607284','2026-10-16 16:06:07'),(73,12,'设备管理','修改','device',1,'更新设备状态：AVAILABLE → BORROWED','2026-10-16 16:06:07'),(74,12,'设备管理','修改','device',1,'更新设备状态：BORROWED → AVAILABLE','2026-10-16 16:06:34'),(75,12,'借还管理','归还设备','borrow_record',22,'归还记录：BOR20261016160607284；借还状态：BORROWED → RETURNED','2026-10-16 16:06:34'),(76,12,'维修管理','提交报修','maintenance_record',14,'维修编号：MNT20261016160634125；故障说明：不好使','2026-10-16 16:06:34'),(77,12,'设备管理','修改','device',1,'更新设备状态：AVAILABLE → MAINTENANCE','2026-10-16 16:06:34'),(78,12,'维修管理','开始维修','maintenance_record',14,'维修编号：MNT20261016160634125；维修状态：PENDING → PROCESSING；处理结果：无；维修费用：0.00','2026-10-16 16:06:44');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_no` varchar(30) NOT NULL COMMENT '预约编号',
  `user_id` bigint NOT NULL COMMENT '申请用户',
  `laboratory_id` bigint NOT NULL COMMENT '预约实验室',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `purpose` varchar(255) NOT NULL COMMENT '使用用途',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING待审核，APPROVED已通过，REJECTED已拒绝，CANCELLED已取消，COMPLETED已完成',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核管理员',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `remark` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `reservation_no` (`reservation_no`),
  KEY `fk_reservation_user` (`user_id`),
  KEY `fk_reservation_auditor` (`audit_user_id`),
  KEY `idx_reservation_conflict` (`laboratory_id`,`start_time`,`end_time`,`status`),
  CONSTRAINT `fk_reservation_auditor` FOREIGN KEY (`audit_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_reservation_lab` FOREIGN KEY (`laboratory_id`) REFERENCES `laboratory` (`id`),
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `chk_reservation_time` CHECK ((`end_time` > `start_time`))
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实验室预约记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,'RES202609001',1,1,'2026-09-02 09:00:00','2026-09-02 11:00:00','数据库课程实验','APPROVED',8,'2026-09-01 10:00:00',NULL,'2026-08-31 20:55:06'),(2,'RES202609002',2,1,'2026-09-02 14:00:00','2026-09-02 16:00:00','小组项目讨论','APPROVED',8,'2026-09-01 11:00:00',NULL,'2026-08-31 20:55:10'),(3,'RES202609003',3,2,'2026-09-03 09:00:00','2026-09-03 12:00:00','软件测试实验','PENDING',NULL,NULL,NULL,'2026-08-31 20:55:13'),(4,'RES202609004',4,3,'2026-09-04 10:00:00','2026-09-04 12:00:00','网络安全实验','APPROVED',11,'2026-09-03 15:00:00',NULL,'2026-08-31 20:55:16'),(5,'RES202609005',5,4,'2026-09-05 08:00:00','2026-09-05 10:00:00','嵌入式开发实验','APPROVED',8,'2026-09-04 16:00:00',NULL,'2026-08-31 20:55:18'),(6,'RES202609006',6,5,'2026-09-06 13:00:00','2026-09-06 15:00:00','人工智能模型训练','APPROVED',11,'2026-09-05 09:00:00',NULL,'2026-08-31 20:55:21'),(7,'RES202609007',7,6,'2026-09-07 09:00:00','2026-09-07 11:00:00','硬件原理实验','REJECTED',8,'2026-09-06 14:00:00','该时段临时设备检修','2026-08-31 20:55:24'),(8,'RES202609008',1,7,'2026-09-08 14:00:00','2026-09-08 17:00:00','物联网课程实践','APPROVED',12,'2026-09-22 21:49:15',NULL,'2026-08-31 20:55:26'),(9,'RES202609009',2,8,'2026-09-09 10:00:00','2026-09-09 12:00:00','移动应用开发','CANCELLED',NULL,NULL,'用户临时取消','2026-08-31 20:55:29'),(10,'RES202609010',3,10,'2026-09-10 15:00:00','2026-09-10 17:00:00','多媒体处理实验','APPROVED',11,'2026-09-09 10:00:00',NULL,'2026-08-31 20:55:41'),(11,'RES20260901135959628',11,1,'2026-09-02 11:00:00','2026-09-02 13:00:00','作业验收','APPROVED',8,'2026-09-01 14:04:31',NULL,'2026-09-01 13:59:59'),(15,'RES20260901150208016',1,3,'2026-09-15 09:00:00','2026-09-15 11:00:00','期末考试','APPROVED',8,'2026-09-16 20:22:00',NULL,'2026-09-01 15:02:08'),(16,'RES20260916201543723',8,1,'2026-09-15 09:00:00','2026-09-15 11:00:00','','APPROVED',8,'2026-09-16 20:22:19',NULL,'2026-09-16 20:15:43'),(17,'RES20260916201551964',8,4,'2026-09-15 09:00:00','2026-09-15 11:00:00','','APPROVED',8,'2026-09-16 20:22:35',NULL,'2026-09-16 20:15:51'),(18,'RES20260920210635659',1,7,'2026-09-02 10:00:00','2026-09-02 12:00:00','实验课','REJECTED',8,'2026-09-20 21:07:07',NULL,'2026-09-20 21:06:35'),(19,'RES20261014173830230',8,1,'2026-10-14 09:00:00','2026-10-15 11:00:00','','PENDING',NULL,NULL,NULL,'2026-10-14 17:38:30'),(20,'RES20261015221338060',1,1,'2026-10-15 22:30:00','2026-10-16 00:30:00','考试','APPROVED',12,'2026-10-15 22:13:51',NULL,'2026-10-15 22:13:38'),(21,'RES20261016122424902',12,1,'2026-10-16 12:30:00','2026-10-16 14:30:00','验收','APPROVED',12,'2026-10-16 12:27:36',NULL,'2026-10-16 12:24:24'),(22,'RES20261016122706462',12,3,'2026-10-16 12:30:00','2026-10-16 14:30:00','自习','PENDING',NULL,NULL,NULL,'2026-10-16 12:27:06'),(23,'RES20261016160329924',1,1,'2026-10-16 16:30:00','2026-10-16 18:30:00','考试','PENDING',NULL,NULL,NULL,'2026-10-16 16:03:29'),(24,'RES20261016160353968',12,3,'2026-10-16 16:30:00','2026-10-16 18:30:00','验收','PENDING',NULL,NULL,NULL,'2026-10-16 16:03:53'),(25,'RES20261016160439230',1,4,'2026-10-16 16:30:00','2026-10-16 18:30:00','考试','PENDING',NULL,NULL,NULL,'2026-10-16 16:04:39');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_reservation_insert` AFTER INSERT ON `reservation` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '预约管理', '提交预约', 'reservation', NEW.id,
 CONCAT('提交预约：', NEW.reservation_no)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_reservation_update` AFTER UPDATE ON `reservation` FOR EACH ROW INSERT INTO operation_log
(user_id, module_name, action_name, target_type, target_id, detail)
VALUES
(@app_user_id, '预约管理', '状态修改', 'reservation', NEW.id,
 CONCAT('预约状态：', OLD.status, ' → ', NEW.status)) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_type` varchar(20) NOT NULL COMMENT 'MENU菜单或ACTION操作',
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_code` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'DEVICE_VIEW','查看设备','MENU','查看设备列表与详情','2026-08-31 20:34:51'),(2,'DEVICE_ADD','新增设备','ACTION','新增设备信息','2026-08-31 20:34:51'),(3,'DEVICE_EDIT','修改设备','ACTION','修改设备信息','2026-08-31 20:34:51'),(4,'DEVICE_DELETE','删除设备','ACTION','删除设备信息','2026-08-31 20:34:51'),(5,'RESERVATION_CREATE','提交预约','ACTION','提交实验室预约申请','2026-08-31 20:34:51'),(6,'RESERVATION_VIEW_SELF','查看个人预约','MENU','查看本人预约记录','2026-08-31 20:34:51'),(7,'RESERVATION_AUDIT','审核预约','ACTION','审核预约申请','2026-08-31 20:34:51'),(8,'BORROW_MANAGE','借还管理','MENU','登记借出与归还','2026-08-31 20:34:51'),(9,'MAINTENANCE_MANAGE','维修管理','MENU','登记和处理维修记录','2026-08-31 20:34:51'),(10,'USER_MANAGE','用户管理','MENU','管理系统用户','2026-08-31 20:34:51'),(11,'ROLE_MANAGE','角色权限管理','MENU','分配角色和权限','2026-08-31 20:34:51'),(12,'REPORT_EXPORT','数据导出','ACTION','导出查询结果','2026-08-31 20:34:51');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1启用，0停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'STUDENT','普通学生','可查询设备、提交预约、查看自己的记录',1,'2026-08-31 20:31:38'),(2,'LAB_ADMIN','实验室管理员','可审核预约、管理设备和借还记录',1,'2026-08-31 20:31:38'),(3,'SYSTEM_ADMIN','系统管理员','可管理用户、角色和权限',1,'2026-08-31 20:31:38');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (1,1,'2026-08-31 20:35:32'),(1,5,'2026-08-31 20:35:32'),(1,6,'2026-08-31 20:35:32'),(2,1,'2026-08-31 20:35:38'),(2,2,'2026-08-31 20:35:38'),(2,3,'2026-08-31 20:35:38'),(2,7,'2026-08-31 20:35:38'),(2,8,'2026-08-31 20:35:38'),(2,9,'2026-08-31 20:35:38'),(2,12,'2026-08-31 20:35:38'),(3,1,'2026-08-31 20:35:46'),(3,2,'2026-08-31 20:35:46'),(3,3,'2026-08-31 20:35:46'),(3,4,'2026-08-31 20:35:46'),(3,5,'2026-08-31 20:35:46'),(3,6,'2026-08-31 20:35:46'),(3,7,'2026-08-31 20:35:46'),(3,8,'2026-08-31 20:35:46'),(3,9,'2026-08-31 20:35:46'),(3,10,'2026-08-31 20:35:46'),(3,11,'2026-08-31 20:35:46'),(3,12,'2026-08-31 20:35:46');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '登录用户名',
  `password` varchar(255) NOT NULL COMMENT '密码哈希值',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1正常，0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'student01','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','张三','13114541122',NULL,1,'2026-08-31 20:45:16','2026-09-21 12:48:09'),(2,'student02','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','李四',NULL,NULL,1,'2026-08-31 20:45:19','2026-08-31 20:45:19'),(3,'student03','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','王五',NULL,NULL,1,'2026-08-31 20:45:21','2026-08-31 20:45:21'),(4,'student04','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','赵六',NULL,NULL,1,'2026-08-31 20:45:25','2026-08-31 20:45:25'),(5,'student05','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','钱七',NULL,NULL,1,'2026-08-31 20:45:27','2026-08-31 20:45:27'),(6,'student06','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','孙八',NULL,NULL,1,'2026-08-31 20:45:28','2026-08-31 20:45:28'),(7,'student07','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','周九',NULL,NULL,1,'2026-08-31 20:45:31','2026-08-31 20:45:31'),(8,'labadmin01','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','陈管理员',NULL,NULL,1,'2026-08-31 20:45:38','2026-08-31 20:45:38'),(11,'labadmin02','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','邵管理员',NULL,NULL,1,'2026-08-31 20:46:28','2026-08-31 20:47:31'),(12,'sysadmin','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','系统管理员',NULL,NULL,1,'2026-08-31 20:46:35','2026-08-31 20:46:35');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_audit_user_update` AFTER UPDATE ON `sys_user` FOR EACH ROW BEGIN
    IF NOT (OLD.username <=> NEW.username)
       OR NOT (OLD.phone <=> NEW.phone)
       OR NOT (OLD.email <=> NEW.email) THEN

        INSERT INTO operation_log
        (
            user_id,
            module_name,
            action_name,
            target_type,
            target_id,
            detail
        )
        VALUES
        (
            COALESCE(@app_user_id, NEW.id),
            '个人资料',
            '修改个人资料',
            'sys_user',
            NEW.id,
            CONCAT(
                '用户：', NEW.username,
                '；修改用户名、电话或邮箱信息'
            )
        );

    END IF;

    IF NOT (OLD.password <=> NEW.password) THEN

        INSERT INTO operation_log
        (
            user_id,
            module_name,
            action_name,
            target_type,
            target_id,
            detail
        )
        VALUES
        (
            COALESCE(@app_user_id, NEW.id),
            '账户安全',
            '修改密码',
            'sys_user',
            NEW.id,
            CONCAT('用户 ', NEW.username, ' 修改了登录密码')
        );

    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,'2026-10-16 15:56:49'),(2,1,'2026-08-31 20:48:20'),(3,1,'2026-08-31 20:48:20'),(4,1,'2026-08-31 20:48:20'),(5,1,'2026-08-31 20:48:20'),(6,1,'2026-08-31 20:48:20'),(7,1,'2026-10-14 17:29:12'),(8,2,'2026-10-14 17:29:00'),(11,2,'2026-10-14 17:27:35'),(12,3,'2026-08-31 20:48:29');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;

--
-- Temporary view structure for view `vw_device_usage`
--

DROP TABLE IF EXISTS `vw_device_usage`;
/*!50001 DROP VIEW IF EXISTS `vw_device_usage`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_device_usage` AS SELECT 
 1 AS `device_id`,
 1 AS `device_no`,
 1 AS `device_name`,
 1 AS `category_name`,
 1 AS `lab_name`,
 1 AS `device_status`,
 1 AS `borrow_count`,
 1 AS `total_borrow_minutes`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_laboratory_overview`
--

DROP TABLE IF EXISTS `vw_laboratory_overview`;
/*!50001 DROP VIEW IF EXISTS `vw_laboratory_overview`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_laboratory_overview` AS SELECT 
 1 AS `laboratory_id`,
 1 AS `lab_code`,
 1 AS `lab_name`,
 1 AS `location`,
 1 AS `capacity`,
 1 AS `lab_status`,
 1 AS `device_count`,
 1 AS `available_device_count`,
 1 AS `borrowed_device_count`,
 1 AS `maintenance_device_count`,
 1 AS `disabled_device_count`,
 1 AS `pending_reservation_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'lab_equipment_db'
--

--
-- Final view structure for view `vw_device_usage`
--

/*!50001 DROP VIEW IF EXISTS `vw_device_usage`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_device_usage` AS select `d`.`id` AS `device_id`,`d`.`device_no` AS `device_no`,`d`.`device_name` AS `device_name`,`c`.`category_name` AS `category_name`,`l`.`lab_name` AS `lab_name`,`d`.`status` AS `device_status`,count(`b`.`id`) AS `borrow_count`,coalesce(sum(timestampdiff(MINUTE,`b`.`borrow_time`,`b`.`actual_return_time`)),0) AS `total_borrow_minutes` from (((`device` `d` join `device_category` `c` on((`d`.`category_id` = `c`.`id`))) join `laboratory` `l` on((`d`.`laboratory_id` = `l`.`id`))) left join `borrow_record` `b` on((`d`.`id` = `b`.`device_id`))) group by `d`.`id`,`d`.`device_no`,`d`.`device_name`,`c`.`category_name`,`l`.`lab_name`,`d`.`status` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_laboratory_overview`
--

/*!50001 DROP VIEW IF EXISTS `vw_laboratory_overview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_laboratory_overview` AS select `l`.`id` AS `laboratory_id`,`l`.`lab_code` AS `lab_code`,`l`.`lab_name` AS `lab_name`,`l`.`location` AS `location`,`l`.`capacity` AS `capacity`,`l`.`status` AS `lab_status`,coalesce(`d`.`device_count`,0) AS `device_count`,coalesce(`d`.`available_device_count`,0) AS `available_device_count`,coalesce(`d`.`borrowed_device_count`,0) AS `borrowed_device_count`,coalesce(`d`.`maintenance_device_count`,0) AS `maintenance_device_count`,coalesce(`d`.`disabled_device_count`,0) AS `disabled_device_count`,coalesce(`r`.`pending_reservation_count`,0) AS `pending_reservation_count` from ((`laboratory` `l` left join (select `device`.`laboratory_id` AS `laboratory_id`,count(0) AS `device_count`,sum((`device`.`status` = 'AVAILABLE')) AS `available_device_count`,sum((`device`.`status` = 'BORROWED')) AS `borrowed_device_count`,sum((`device`.`status` = 'MAINTENANCE')) AS `maintenance_device_count`,sum((`device`.`status` = 'DISABLED')) AS `disabled_device_count` from `device` group by `device`.`laboratory_id`) `d` on((`d`.`laboratory_id` = `l`.`id`))) left join (select `reservation`.`laboratory_id` AS `laboratory_id`,count(0) AS `pending_reservation_count` from `reservation` where (`reservation`.`status` = 'PENDING') group by `reservation`.`laboratory_id`) `r` on((`r`.`laboratory_id` = `l`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-20 16:43:04
