CREATE DATABASE IF NOT EXISTS smart_mes
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_mes;


CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY,
                          username VARCHAR(64) NOT NULL UNIQUE,
                          password VARCHAR(100) NOT NULL,
                          employee_no VARCHAR(64),
                          real_name VARCHAR(64),
                          card_no VARCHAR(64),
                          status VARCHAR(16) NOT NULL,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE sys_role (
                          id BIGINT PRIMARY KEY,
                          role_code VARCHAR(64) NOT NULL UNIQUE,
                          role_name VARCHAR(64) NOT NULL
);


CREATE TABLE sys_permission (
                                id BIGINT PRIMARY KEY,
                                permission_code VARCHAR(64) NOT NULL UNIQUE,
                                permission_name VARCHAR(64) NOT NULL
);


CREATE TABLE sys_user_role (
                               user_id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL,
                               PRIMARY KEY (user_id, role_id)
);


CREATE TABLE sys_role_permission (
                                     role_id BIGINT NOT NULL,
                                     permission_id BIGINT NOT NULL,
                                     PRIMARY KEY (role_id, permission_id)
);


CREATE TABLE mes_workshop (
                              id BIGINT PRIMARY KEY,
                              workshop_code VARCHAR(64) NOT NULL UNIQUE,
                              workshop_name VARCHAR(100) NOT NULL,
                              status VARCHAR(16) NOT NULL
);


CREATE TABLE mes_product (
                             id BIGINT PRIMARY KEY,
                             product_code VARCHAR(64) NOT NULL UNIQUE,
                             product_name VARCHAR(100) NOT NULL,
                             specification VARCHAR(255),
                             status VARCHAR(16) NOT NULL
);


CREATE TABLE mes_device (
                            id BIGINT PRIMARY KEY,
                            device_code VARCHAR(64) NOT NULL UNIQUE,
                            device_name VARCHAR(100) NOT NULL,
                            device_type VARCHAR(64),
                            workshop_id BIGINT,
                            ip_address VARCHAR(64),
                            port INT,
                            protocol VARCHAR(32),
                            status VARCHAR(16),
                            last_online_time DATETIME,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mes_workstation (
                                 id BIGINT PRIMARY KEY,
                                 workstation_code VARCHAR(64) NOT NULL UNIQUE,
                                 workstation_name VARCHAR(100),
                                 workshop_id BIGINT,
                                 device_id BIGINT,
                                 status VARCHAR(16)
);


CREATE TABLE mes_production_order (
                                      id BIGINT PRIMARY KEY,
                                      order_no VARCHAR(64) NOT NULL UNIQUE,
                                      erp_order_no VARCHAR(64) UNIQUE,
                                      product_id BIGINT NOT NULL,
                                      plan_quantity DECIMAL(18, 3) NOT NULL,
                                      completed_quantity DECIMAL(18, 3) NOT NULL DEFAULT 0,
                                      priority VARCHAR(32),
                                      status VARCHAR(16) NOT NULL,
                                      planned_start_time DATETIME,
                                      planned_end_time DATETIME,
                                      actual_start_time DATETIME,
                                      actual_end_time DATETIME,
                                      version INT NOT NULL DEFAULT 0,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mes_process_route (
                                   id BIGINT PRIMARY KEY,
                                   route_code VARCHAR(64) NOT NULL,
                                   route_name VARCHAR(100),
                                   product_id BIGINT NOT NULL,
                                   version INT NOT NULL,
                                   status VARCHAR(16) NOT NULL,
                                   UNIQUE (route_code, version)
);


CREATE TABLE mes_process_step (
                                  id BIGINT PRIMARY KEY,
                                  route_id BIGINT NOT NULL,
                                  step_no INT NOT NULL,
                                  process_code VARCHAR(64),
                                  process_name VARCHAR(100),
                                  workstation_id BIGINT,
                                  standard_duration DECIMAL(12, 2),
                                  parameter_json JSON,
                                  UNIQUE (route_id, step_no)
);


CREATE TABLE mes_work_order (
                                id BIGINT PRIMARY KEY,
                                work_order_no VARCHAR(64) NOT NULL UNIQUE,
                                production_order_id BIGINT,
                                process_route_id BIGINT,
                                workshop_id BIGINT,
                                plan_quantity DECIMAL(18, 3),
                                completed_quantity DECIMAL(18, 3) DEFAULT 0,
                                status VARCHAR(16),
                                current_step_no INT,
                                planned_start_time DATETIME,
                                planned_end_time DATETIME,
                                actual_start_time DATETIME,
                                actual_end_time DATETIME
);


CREATE TABLE mes_device_data (
                                 id BIGINT PRIMARY KEY,
                                 device_id BIGINT NOT NULL,
                                 temperature DECIMAL(10, 2),
                                 pressure DECIMAL(10, 2),
                                 speed DECIMAL(10, 2),
                                 running_status VARCHAR(16),
                                 alarm_code VARCHAR(64),
                                 collect_time DATETIME NOT NULL,
                                 INDEX idx_device_collect (device_id, collect_time)
);


CREATE TABLE mes_device_alarm (