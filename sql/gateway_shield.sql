/*
 Navicat Premium Data Transfer

 Source Server         : localhost-3306
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : gateway_shield

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 07/05/2024 20:33:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gateway_shield_log
-- ----------------------------
CREATE TABLE `gateway_shield_log`  (
  `gateway_shield_log_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `request_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求ID',
  `scheme` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '协议',
  `host_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '域名',
  `host_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'IP',
  `network` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网段',
  `system_organization` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '运营商',
  `system_number` int(0) NULL DEFAULT NULL COMMENT '运营商ID',
  `continent_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大陆代码',
  `continent_geo_name_id` int(0) NULL DEFAULT NULL COMMENT '大陆地理ID',
  `continent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大陆名称',
  `country_iso_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家/地区ISO代码',
  `country_geo_name_id` int(0) NULL DEFAULT NULL COMMENT '国家/地区地理ID',
  `country_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家/地区名称',
  `is_in_european_union` tinyint(1) NULL DEFAULT NULL COMMENT '是否是欧盟成员国',
  `subdivision_iso_codes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区细分ISO代码，如：省份等',
  `subdivision_geo_name_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区细分地理ID，如：省份等',
  `subdivision_names` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区细分名称，如：省份等',
  `city_geo_name_id` int(0) NULL DEFAULT NULL COMMENT '城市地理ID',
  `city_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Host',
  `port` int(0) NOT NULL COMMENT '端口',
  `path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路径',
  `query` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '参数',
  `raw_query` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '参数',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览器UA',
  `referer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '来源',
  `headers_json` json NOT NULL COMMENT '请求头',
  `multipart_data_json` json NOT NULL COMMENT '',
  `year` int(0) NOT NULL COMMENT '年',
  `month` int(0) NOT NULL COMMENT '月',
  `day` int(0) NOT NULL COMMENT '日',
  `hour` int(0) NOT NULL COMMENT '时',
  `minute` int(0) NOT NULL COMMENT '分',
  `second` int(0) NOT NULL COMMENT '秒',
  `create_date` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间，不为空，数据库自动生成',
  `update_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间，未更新时为空',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除，0 未删除，1 删除，MySQL 默认值 0，不为 NULL，注解@TableLogic。',
  PRIMARY KEY (`gateway_shield_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
