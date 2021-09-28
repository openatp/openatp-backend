-- 创建数据库

create schema if not exists `openatp` charset utf8mb4 collate utf8mb4_unicode_ci;
use `openatp`;

-- 创建表

CREATE TABLE `project`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `project_env_variable`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `default_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_id`    bigint                                  DEFAULT NULL,
    `variable_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `project_request`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `content_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `header`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `method`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `param`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `path`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_id`   bigint                                  DEFAULT NULL,
    `timeout`      bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `project_request_argument`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `argument_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_id`    bigint                                  DEFAULT NULL,
    `request_id`    bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `project_request_response`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `field_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_id` bigint                                  DEFAULT NULL,
    `request_id` bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `project_server`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `project_id`  bigint                                  DEFAULT NULL,
    `server_addr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `server_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_id`         bigint                                  DEFAULT NULL,
    `project_request_id` bigint                                  DEFAULT NULL,
    `type`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case_execute_detail`
(
    `id`                     bigint                          NOT NULL AUTO_INCREMENT,
    `exec_check_info`        text COLLATE utf8mb4_unicode_ci,
    `exec_check_result`      int                                     DEFAULT NULL,
    `execute_id`             varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `http_request`           text COLLATE utf8mb4_unicode_ci NOT NULL,
    `http_request_time`      int                             NOT NULL,
    `http_response_body`     text COLLATE utf8mb4_unicode_ci NOT NULL,
    `http_response_code`     int                             NOT NULL,
    `save_env_variable_info` text COLLATE utf8mb4_unicode_ci,
    `test_case_request_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case_execute_history`
(
    `id`                          varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `execute_datetime`            varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `execute_status`              int                                     DEFAULT NULL,
    `execute_status_detail`       varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_server_name`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `request_check_correct_count` int                                     DEFAULT NULL,
    `request_check_correct_rate`  double                                  DEFAULT NULL,
    `request_success_count`       int                                     DEFAULT NULL,
    `request_success_rate`        double                                  DEFAULT NULL,
    `requestt_total_count`        int                                     DEFAULT NULL,
    `test_case_id`                bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case_request`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `arguments`          varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name`               varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `project_request_id` bigint                                  DEFAULT NULL,
    `test_case_id`       bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case_request_exec_check`
(
    `id`                        bigint NOT NULL AUTO_INCREMENT,
    `field_name`                varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `field_path`                varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `test_case_request_id`      bigint                                  DEFAULT NULL,
    `want_response_field_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci

CREATE TABLE `test_case_request_save_env_variable`
(
    `id`                              bigint NOT NULL AUTO_INCREMENT,
    `project_env_variable_id`         bigint                                  DEFAULT NULL,
    `project_env_variable_value_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `test_case_request_id`            bigint                                  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
