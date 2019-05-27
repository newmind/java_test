CREATE DATABASE `test_jgkim`
/*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `test_jgkim`.`random_src` (
  `create_time` datetime(3) NOT NULL,
  `random` int(11) NOT NULL,
  PRIMARY KEY (`create_time`),
  UNIQUE KEY `create_time_UNIQUE` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `test_jgkim`.`random_dst` (
  `create_time` datetime(3) NOT NULL,
  `random` int(11) NOT NULL,
  PRIMARY KEY (`create_time`),
  UNIQUE KEY `create_time_UNIQUE` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

