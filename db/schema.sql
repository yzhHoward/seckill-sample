/*
 *  mysql-v: 5.7.22
 */

-- 创建数据库
-- CREATE DATABASE seckill DEFAULT CHARACTER SET utf8;


DROP TABLE IF EXISTS `commodity`;
DROP TABLE IF EXISTS `order`;

-- 创建秒杀商品表
CREATE TABLE `commodity`(
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar (1000) NOT NULL COMMENT '商品名',
  `price` decimal (10,2) NOT NULL COMMENT '商品价格',
  `stock` bigint NOT NULL COMMENT '剩余库存数量',
  PRIMARY KEY (`id`)
) CHARSET=utf8mb4 ENGINE=InnoDB COMMENT '商品表';

-- 创建秒杀订单表
CREATE TABLE `order`(
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `commodity_id` bigint NOT NULL COMMENT '秒杀商品ID',
  `money` decimal (10, 2) NOT NULL COMMENT '支付金额',
  `phone` bigint NOT NULL COMMENT '用户手机号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) CHARSET=utf8mb4 ENGINE=InnoDB COMMENT '秒杀订单表';