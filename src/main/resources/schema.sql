CREATE TABLE IF NOT EXISTS `user` (
  `uid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`)
) DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `board` (
  `bid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `category` int(11) NOT NULL,
  `req_recent` bigint(20) NOT NULL DEFAULT '0',
  `req_today` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bid`)
) DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `post` (
  `pid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) unsigned NOT NULL,
  `bid` bigint(20) unsigned NOT NULL,
  `category` int(11) NOT NULL,
  `title` varchar(128) NOT NULL,
  `content` mediumtext NOT NULL,
  `views` bigint(20) unsigned NOT NULL DEFAULT '0',
  `likes` bigint(20) unsigned NOT NULL DEFAULT '0',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cmtCnt` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`pid`),
  KEY `foreign_uid` (`uid`),
  KEY `idx_post` (`bid`,`pid`),
  KEY `idx_board_best` (`bid`,`created_date`),
  CONSTRAINT `post_board_bid` FOREIGN KEY (`bid`) REFERENCES `board` (`bid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `post_user_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE NO ACTION ON UPDATE CASCADE
) DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `comment` (
  `cid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) unsigned NOT NULL,
  `pid` bigint(20) unsigned NOT NULL,
  `content` text NOT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cid`),
  KEY `foreign_uid` (`uid`),
  KEY `idx_post` (`pid`,`cid`),
  CONSTRAINT `comment_post_bid` FOREIGN KEY (`pid`) REFERENCES `post` (`pid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_user_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) DEFAULT CHARSET=utf8;

