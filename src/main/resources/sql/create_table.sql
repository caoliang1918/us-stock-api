# noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE `stock_product`
(
    `id`           bigint(20)         DEFAULT NULL,
    `cts`          timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `symbol`       varchar(255)       DEFAULT NULL,
    `sec_type`     varchar(255)       DEFAULT NULL,
    `unit`         varchar(255)       DEFAULT NULL,
    `name`         varchar(255)       DEFAULT NULL,
    `exchange`     varchar(255)       DEFAULT NULL,
    `market`       varchar(255)       DEFAULT NULL,
    `time_zone`    varchar(255)       DEFAULT NULL,
    `start_time`   time               DEFAULT NULL,
    `end_time`     time               DEFAULT NULL,
    `before_time`  time               DEFAULT NULL,
    `after_time`   time               DEFAULT NULL,
    `pre_close`    varchar(255)       DEFAULT NULL,
    `pre_start`    varchar(255)       DEFAULT NULL,
    `high_price`   varchar(255)       DEFAULT NULL,
    `low_price`    varchar(255)       DEFAULT NULL,
    `change`       tinytext,
    `float_shares` bigint(20)         DEFAULT NULL,
    `shares`       bigint(20)         DEFAULT NULL,
    `market_value` bigint(20)         DEFAULT NULL,
    `shortable`    int(11)            DEFAULT NULL,
    `status`       int(11)            DEFAULT NULL,
    UNIQUE KEY `unq_idx_code` (`symbol`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE `stock_query`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `cts`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本地时间',
    `uts`       timestamp(3) NULL     DEFAULT NULL COMMENT '交易所时间',
    `symbol`    varchar(255) NOT NULL COMMENT '股票代码',
    `avg_price` varchar(255) NOT NULL DEFAULT '0' COMMENT '交易均价',
    `price`     varchar(255) NOT NULL DEFAULT '0' COMMENT '当前价格',
    `volume`    int(11)               DEFAULT NULL COMMENT '成交量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_idx_symbol` (`symbol`, `price`, `uts`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `stock_options`
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `cts`    datetime   NOT NULL COMMENT '创建时间',
    `uts`    datetime    DEFAULT NULL COMMENT '期权时间',
    `price`  varchar(10) DEFAULT NULL COMMENT '价格',
    `symbol` varchar(20) DEFAULT NULL COMMENT '代码',
    `strike` varchar(10) DEFAULT NULL COMMENT '行权期',
    `right`  varchar(10) DEFAULT NULL COMMENT 'call/put',
    `expiry` varchar(20) DEFAULT NULL COMMENT '行权价格',
    `volume` int(11)     DEFAULT NULL COMMENT '交易数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_idx_option` (`symbol`, `expiry`, `right`, `strike`, `price`, `volume`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- -----------------------
-- 期权查询sql
select symbol, expiry, `right`, strike, sum(volume) as t
from stock_options
where symbol = 'SPY'
  and expiry = '2019-03-15'
  and DATE_FORMAT(uts, '%Y-%m-%d') = '2019-03-15'
group by symbol, expiry, `right`, strike
order by t desc;



select `id`,
       `cts`,
       `uts`,
       `price`,
       `symbol`,
       `strike`,
       `right`,
       `expiry`,
       `volume`
from stock_options
where symbol = 'SPY'
  and strike = '281.0'
  and `right` = 'PUT'
  and expiry = '2019-03-15'
  and DATE_FORMAT(uts, '%Y-%m-%d') = '2019-03-15'
order by id
             - - - - - - - - - - - - - - - - - - - - - - - --