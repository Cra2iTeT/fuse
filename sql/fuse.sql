/*
 Navicat Premium Data Transfer

 Source Server         : master
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.150.13:3306
 Source Schema         : fuse

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 01/05/2023 17:19:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for china_city
-- ----------------------------
DROP TABLE IF EXISTS `china_city`;
CREATE TABLE `china_city`  (
  `location_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `location_name_en` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '城市英文名',
  `location_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '城市中文名',
  PRIMARY KEY (`location_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of china_city
-- ----------------------------

-- ----------------------------
-- Table structure for city_weather_each_hour
-- ----------------------------
DROP TABLE IF EXISTS `city_weather_each_hour`;
CREATE TABLE `city_weather_each_hour`  (
  `location_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `time` bigint(0) NOT NULL COMMENT '时间戳(Long)',
  `temperature` tinyint(0) NOT NULL COMMENT '气温(摄氏度)',
  `wind_direction` int(0) NOT NULL COMMENT '风向(360°)',
  `pressure` int(0) NOT NULL COMMENT '气压',
  `wind_speed` tinyint(0) NOT NULL COMMENT '风速',
  `humidity` tinyint(0) NOT NULL COMMENT '湿度(百分比,45 50)',
  PRIMARY KEY (`location_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of city_weather_each_hour
-- ----------------------------

-- ----------------------------
-- Table structure for error_log
-- ----------------------------
DROP TABLE IF EXISTS `error_log`;
CREATE TABLE `error_log`  (
  `log_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `error_time` bigint(0) NOT NULL COMMENT '出错时间',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '错误信息',
  `error_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `log` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of error_log
-- ----------------------------

-- ----------------------------
-- Table structure for predict_result
-- ----------------------------
DROP TABLE IF EXISTS `predict_result`;
CREATE TABLE `predict_result`  (
  `time` bigint(0) NOT NULL,
  `region` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `fan_id` int(0) NOT NULL COMMENT '风机id',
  `power` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `yd_15` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of predict_result
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
