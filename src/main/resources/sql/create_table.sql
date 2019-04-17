CREATE TABLE `stock_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cts` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `symbol` varchar(255) DEFAULT NULL COMMENT '股票代码',
  `sec_type` varchar(255) DEFAULT NULL COMMENT 'STK 股票, OPT 期权，WAR窝轮，IOPT牛熊证, FUT期货',
  `unit` varchar(255) DEFAULT NULL COMMENT '单价单位',
  `name` varchar(255) DEFAULT NULL COMMENT '股票名称',
  `exchange` varchar(255) DEFAULT NULL COMMENT '交易所',
  `market` varchar(255) DEFAULT NULL COMMENT '发行地区',
  `time_zone` varchar(255) DEFAULT NULL COMMENT '时区',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `before_time` time DEFAULT NULL COMMENT '盘前时间',
  `after_time` time DEFAULT NULL COMMENT '盘后时间',
  `pre_close` varchar(255) DEFAULT NULL COMMENT '昨日收盘价',
  `pre_start` varchar(255) DEFAULT NULL COMMENT '今日开盘价',
  `high_price` varchar(255) DEFAULT NULL COMMENT '最高',
  `low_price` varchar(255) DEFAULT NULL COMMENT '最低',
  `change` varchar(10) DEFAULT NULL COMMENT '涨跌幅度',
  `float_shares` bigint(20) DEFAULT NULL COMMENT '流通股本',
  `shares` bigint(20) DEFAULT NULL COMMENT '总股本',
  `market_value` bigint(20) DEFAULT NULL COMMENT '市值',
  `shortable` int(11) DEFAULT NULL COMMENT '非0表示该股票可做空',
  `status` int(1) DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_idx_code` (`symbol`),
  KEY `idx_market_value` (`market_value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `stock_query` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uts` timestamp ,
  `symbol` varchar(255) NOT NULL,
  `avg_price` varchar(255) NOT NULL DEFAULT '0',
  `price` varchar(255) NOT NULL DEFAULT '0',
  `volume` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_idx_symbol` (`symbol`,`uts`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `stock_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` datetime NOT NULL COMMENT '创建时间',
  `uts` datetime DEFAULT NULL COMMENT '期权时间',
  `price` varchar(10) DEFAULT NULL COMMENT '价格',
  `symbol` varchar(20) DEFAULT NULL COMMENT '代码',
  `strike` varchar(10) DEFAULT NULL COMMENT '行权期',
  `right` varchar(10) DEFAULT NULL COMMENT 'call/put',
  `expiry` varchar(20) DEFAULT NULL COMMENT '行权价格',
  `volume` int(11) DEFAULT NULL COMMENT '交易数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_idx_option` (`symbol`,`expiry`,`right`,`strike`,`price`,`volume`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



-------------------------
-- 期权查询sql
	select symbol , expiry , `right` , strike , sum(volume) as t from stock_options
	where symbol = 'SPY' and expiry = '2019-03-15' and DATE_FORMAT(uts ,'%Y-%m-%d') = '2019-03-15'  group by symbol , expiry , `right` , strike order by t desc



	select `id`, `cts`, `uts`, `price`, `symbol`, `strike`, `right`, `expiry`, `volume` from stock_options where
	symbol = 'SPY' and strike = '281.0' and `right` = 'PUT' and expiry = '2019-03-15'  and DATE_FORMAT(uts ,'%Y-%m-%d') = '2019-03-15'  order by id

-------------------------