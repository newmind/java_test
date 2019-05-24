CREATE DATABASE `test_random`
/*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `test_random`.`random_src` (
  `create_time` TIMESTAMP NOT NULL,
  `random_srccol` INT NOT NULL,
  PRIMARY KEY (`create_time`),
  UNIQUE INDEX `create_time_UNIQUE` (`create_time` ASC) VISIBLE);

CREATE TABLE `test_random`.`random_dst` (
  `create_time` TIMESTAMP NOT NULL,
  `random_srccol` INT NOT NULL,
  PRIMARY KEY (`create_time`),
  UNIQUE INDEX `create_time_UNIQUE` (`create_time` ASC) VISIBLE);

