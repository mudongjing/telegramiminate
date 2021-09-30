create database IF NOT EXISTS tele;
use tele;
drop table if exists user;
drop table if exists channel;
drop table if exists `group`;
drop table if exists `message-123`;
-- 用户数据表-----------------
create table IF NOT EXISTS `user` (
      `user_id`  INT(10) NOT NULL AUTO_INCREMENT,
      `user_name` VARCHAR(255) not null,
      `user_password` VARCHAR(255),
      `user_friends` VARCHAR(255)  default null,
      `friends_type` INT(1) not null,
      `user_groups` VARCHAR(255) default null,
      `group_type` INT(1) not null,
      `join_group` VARCHAR(255) DEFAULT NULL ,
      `join_type` INT(1) NOT NULL ,
      UNIQUE KEY `user_key`(`user_name`,`user_id`),
      PRIMARY KEY(`user_id`)
)engine =InnoDB default charset=utf8;

-- 群组数据表------------
create table IF NOT EXISTS `groupItemTable` (
       `group_id`  INT(10) not null auto_increment ,
       `group_name` VARCHAR(255) not null,
       `group_creator` INT(10) not null,
       `group_members` VARCHAR(255) not null,
       `group_message` VARCHAR(255) not null,
       `group_description` VARCHAR(255) DEFAULT NULL,
       UNIQUE KEY `group_key`(`group_name`,`group_creator`),
       PRIMARY KEY(`group_id`)
)engine =InnoDB default charset=utf8;

-- 消息数据表
create table  IF NOT EXISTS `message-123` (
      `message_id` INT(10) NOT NULL  auto_increment,
      `message_content` VARCHAR(255) NOT NULL,
      `message_type` INT(2)  NOT NULL,
      `message_creator` INT(10) NOT NULL,
      `message_date`  DATE NOT NULL,
      `message_time` TIME NOT NULL,
      PRIMARY KEY(`message_id`)
)engine=InnoDB default charset=utf8;