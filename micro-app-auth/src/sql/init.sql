/*
Navicat MySQL Data Transfer

Source Server         : 47.106.189.245
Source Server Version : 50724
Source Host           : 47.106.189.245:3306
Source Database       : uaams

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2020-03-11 14:57:18
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `uaam_auth_access`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_auth_access`;
CREATE TABLE `uaam_auth_access` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `org_info` varchar(100) NOT NULL,
  `uid` varchar(50) NOT NULL,
  `passwd` varchar(100) NOT NULL,
  `ip_pattern` varchar(100) DEFAULT NULL,
  `remove_flag` int(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uaam_auth_access
-- ----------------------------
INSERT INTO `uaam_auth_access` VALUES ('1', '创业慧康', 'cy_user1', 'e6e061838856bf47e1de730719fb2609', null, '0');

-- ----------------------------
-- Table structure for `uaam_org`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_org`;
CREATE TABLE `uaam_org` (
  `id` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `parentid` int(6) DEFAULT NULL,
  `orgcode` varchar(20) DEFAULT NULL,
  `orgname` varchar(60) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(机构表)';

-- ----------------------------
-- Records of uaam_org
-- ----------------------------
INSERT INTO `uaam_org` VALUES ('1', null, 'PDY10000-1', '遵义市卫计委', null);

-- ----------------------------
-- Table structure for `uaam_resources`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_resources`;
CREATE TABLE `uaam_resources` (
  `resid` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `parentid` int(6) DEFAULT NULL,
  `restype` varchar(10) DEFAULT NULL,
  `resname` varchar(100) DEFAULT NULL,
  `reslevel` int(2) DEFAULT NULL,
  `resurl` varchar(100) DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `resdomain` varchar(30) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  `resorder` int(5) DEFAULT NULL,
  PRIMARY KEY (`resid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(资源表)';

-- ----------------------------
-- Records of uaam_resources
-- ----------------------------
INSERT INTO `uaam_resources` VALUES ('1', null, 'PLATFORM', '人口健康信息平台', '0', null, null, 'platform', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('2', '1', 'APP', '系统1', '1', 'http://localhost:8080/ssoClient', '/appImages/jd.png', 'ssoclient', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('3', '1', 'APP', '系统2', '1', 'http://www.wy-tcl.xyz:8080/ssoClient1', '/appImages/dy.png', 'ssoclient1', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('4', '1', 'APP', '统一用户授权管理系统', '1', 'http://localhost:8080/uaams/ssoLogon.action', '/appImages/wx.png', 'uaams', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('5', '4', 'MENU', '资源目录', '2', null, null, 'uaams', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('6', '5', 'MENU', '配置管理', '3', null, null, 'uaams', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('7', '6', 'MENU', '用户管理', '4', 'config/user.action', null, 'uaams', null, '1', '1');
INSERT INTO `uaam_resources` VALUES ('8', '6', 'MENU', '角色管理', '4', 'config/role.action', null, 'uaams', null, '1', '2');
INSERT INTO `uaam_resources` VALUES ('9', '6', 'MENU', '资源管理', '4', 'config/resource.action', null, 'uaams', null, '1', '3');

-- ----------------------------
-- Table structure for `uaam_roleresources`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_roleresources`;
CREATE TABLE `uaam_roleresources` (
  `refid` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `roleid` int(6) DEFAULT NULL,
  `resid` int(6) DEFAULT NULL,
  PRIMARY KEY (`refid`) USING BTREE,
  KEY `fk_rr_resid` (`resid`),
  KEY `fk_rr_rid` (`roleid`),
  CONSTRAINT `fk_rr_resid` FOREIGN KEY (`resid`) REFERENCES `uaam_resources` (`resid`),
  CONSTRAINT `fk_rr_rid` FOREIGN KEY (`roleid`) REFERENCES `uaam_roles` (`roleid`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(角色资源表)';

-- ----------------------------
-- Records of uaam_roleresources
-- ----------------------------
INSERT INTO `uaam_roleresources` VALUES ('1', '1', '1');
INSERT INTO `uaam_roleresources` VALUES ('2', '1', '2');
INSERT INTO `uaam_roleresources` VALUES ('3', '1', '3');
INSERT INTO `uaam_roleresources` VALUES ('4', '1', '4');
INSERT INTO `uaam_roleresources` VALUES ('5', '1', '5');
INSERT INTO `uaam_roleresources` VALUES ('6', '1', '6');
INSERT INTO `uaam_roleresources` VALUES ('7', '1', '7');
INSERT INTO `uaam_roleresources` VALUES ('8', '1', '8');
INSERT INTO `uaam_roleresources` VALUES ('9', '1', '9');

-- ----------------------------
-- Table structure for `uaam_roles`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_roles`;
CREATE TABLE `uaam_roles` (
  `roleid` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `rolename` varchar(50) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`roleid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(角色表)';

-- ----------------------------
-- Records of uaam_roles
-- ----------------------------
INSERT INTO `uaam_roles` VALUES ('1', '系统管理员', '最高权限用户');

-- ----------------------------
-- Table structure for `uaam_userresources`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_userresources`;
CREATE TABLE `uaam_userresources` (
  `refid` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `userid` varchar(50) DEFAULT NULL,
  `resid` int(6) DEFAULT NULL,
  PRIMARY KEY (`refid`) USING BTREE,
  KEY `fk_ures_resid` (`resid`),
  CONSTRAINT `fk_ures_resid` FOREIGN KEY (`resid`) REFERENCES `uaam_resources` (`resid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(用户资源表)';

-- ----------------------------
-- Records of uaam_userresources
-- ----------------------------

-- ----------------------------
-- Table structure for `uaam_userroles`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_userroles`;
CREATE TABLE `uaam_userroles` (
  `refid` int(6) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `userid` varchar(50) DEFAULT NULL,
  `roleid` int(6) DEFAULT NULL,
  PRIMARY KEY (`refid`) USING BTREE,
  KEY `fk_ur_rid` (`roleid`),
  CONSTRAINT `fk_ur_rid` FOREIGN KEY (`roleid`) REFERENCES `uaam_roles` (`roleid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(用户角色关联表)';

-- ----------------------------
-- Records of uaam_userroles
-- ----------------------------
INSERT INTO `uaam_userroles` VALUES ('1', '1', '1');

-- ----------------------------
-- Table structure for `uaam_users`
-- ----------------------------
DROP TABLE IF EXISTS `uaam_users`;
CREATE TABLE `uaam_users` (
  `userid` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `username` varchar(20) DEFAULT NULL,
  `personname` varchar(20) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `password` varchar(60) DEFAULT NULL,
  `cardnum` varchar(20) DEFAULT NULL,
  `organizecode` varchar(20) DEFAULT NULL,
  `status` varchar(5) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `pwderrortimes` int(2) DEFAULT NULL,
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='统一认证授权管理(用户表)';

-- ----------------------------
-- Records of uaam_users
-- ----------------------------
INSERT INTO `uaam_users` VALUES ('1', 'system', '管理员', '1', 'e6e061838856bf47e1de730719fb2609', '001', 'PDY10000-1', '0', null, '0');
INSERT INTO `uaam_users` VALUES ('2', 'wuy', null, null, '202cb962ac59075b964b07152d234b70', null, null, '0', null, null);
INSERT INTO `uaam_users` VALUES ('3', 'user1', null, null, '202cb962ac59075b964b07152d234b70', null, null, '0', null, null);
INSERT INTO `uaam_users` VALUES ('4', 'user2', null, null, '202cb962ac59075b964b07152d234b70', null, null, '0', null, null);
