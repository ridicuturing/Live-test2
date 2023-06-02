
CREATE TABLE if not exists `action` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `message_sn` VARCHAR(255),
                          `chat_sn` VARCHAR(255),
                          `action_content` VARCHAR(255),
                          `result` VARCHAR(255),
                          `state` BIGINT,
                          `create_time` TIMESTAMP,
                          `update_time` TIMESTAMP
);

CREATE TABLE if not exists `chat_info` (
                             `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                             `chat_sn` VARCHAR(255),
                             `chat_name` VARCHAR(255),
                             `parent_chat_id` BIGINT,
                             `id_tree_path` VARCHAR(255),
                             `system_prompt` VARCHAR(255),
                             `user_sn` VARCHAR(255),
                             `create_time` TIMESTAMP,
                             `update_time` TIMESTAMP
);

CREATE TABLE if not exists `chat_message` (
                                `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                `message_sn` VARCHAR(255),
                                `user_sn` VARCHAR(255),
                                `chat_sn` VARCHAR(255),
                                `role` VARCHAR(255),
                                `type` INT,
                                `content` TEXT,
                                `tokens` INT,
                                `action_type` VARCHAR(255),
                                `create_time` TIMESTAMP,
                                `update_time` TIMESTAMP
);

CREATE TABLE if not exists `chat_user` (
                             `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                             `session_id` VARCHAR(255),
                             `user_sn` VARCHAR(255),
                             `role` VARCHAR(255),
                             `create_time` TIMESTAMP,
                             `update_time` TIMESTAMP
);

CREATE TABLE if not exists `micro_service_do` (
                                    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    `chat_sn` VARCHAR(255),
                                    `name` VARCHAR(255),
                                    `content` VARCHAR(255),
                                    `content_type` VARCHAR(255),
                                    `state` VARCHAR(255),
                                    `create_time` TIMESTAMP,
                                    `update_time` TIMESTAMP
);
