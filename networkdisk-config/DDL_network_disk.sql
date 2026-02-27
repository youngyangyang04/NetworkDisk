  -- ----------------------------
  -- Table structure for user
  -- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id`                bigint                                                          NOT NULL COMMENT '用户id',
                        `nick_name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL DEFAULT '' COMMENT '用户昵称',
                        `use_space`         bigint                                                          NOT NULL DEFAULT 0 COMMENT '已使用空间(字节)',
                        `total_space`       bigint                                                          NOT NULL DEFAULT 1073741824 COMMENT '总空间(字节)',
                        `email`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT '用户邮箱',
                        `password_hash`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT '密码哈希',
                        `invite_code`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci    DEFAULT NULL COMMENT '邀请码(兼容旧查询)',
                        `telephone`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci    DEFAULT NULL COMMENT '手机号(兼容旧查询)',
                        `last_login_time`   datetime                                                        DEFAULT NULL COMMENT '最后登录时间',
                        `profile_photo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '头像URL',
                        `gmt_create`        datetime                                                        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `gmt_modified`      datetime                                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                        `deleted`           int                                                             NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                        `lock_version`      int                                                             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uk_email` (`email`) USING BTREE,
                        KEY `idx_nick_name` (`nick_name`) USING BTREE,
                        KEY `idx_telephone` (`telephone`) USING BTREE,
                        KEY `idx_invite_code` (`invite_code`) USING BTREE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='用户信息表（适配当前代码）'
  ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
                        `id`                        bigint                                                 NOT NULL COMMENT '文件id',
                        `filename`                  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件名称',
                        `real_path`                 varchar(700) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件物理路径',
                        `file_size`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件实际大小',
                        `file_size_desc`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件大小展示字符',
                        `file_suffix`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件后缀',
                        `file_preview_content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '预览Content-Type',
                        `identifier`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件唯一标识',
                        `create_user`               bigint                                                 NOT NULL COMMENT '创建人',
                        `gmt_create`                datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `gmt_modified`              datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                        `create_time`               datetime GENERATED ALWAYS AS (`gmt_create`) STORED COMMENT '兼容旧Mapper字段',
                        `deleted`                   int                                                    NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                        `lock_version`              int                                                    NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `idx_file_create_user_identifier` (`create_user`, `identifier`) USING BTREE,
                        KEY `idx_file_identifier` (`identifier`) USING BTREE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_bin COMMENT='物理文件信息表'
  ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for user_file
-- ----------------------------
DROP TABLE IF EXISTS `user_file`;
CREATE TABLE `user_file` (
                             `id`               bigint                                                  NOT NULL COMMENT '文件记录ID',
                             `file_id`          bigint      GENERATED ALWAYS AS (`id`) STORED COMMENT '兼容旧Mapper字段，等同id',
                             `user_id`          bigint                                                  NOT NULL COMMENT '用户ID',
                             `parent_id`        bigint                                                  NOT NULL COMMENT '上级文件夹ID，顶级为0',
                             `real_file_id`     bigint                                                  DEFAULT NULL COMMENT '真实文件id（文件夹为NULL）',
                             `filename`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '文件名',
                             `folder_flag`      tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否文件夹（0否 1是）',
                             `file_size_desc`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '--' COMMENT '文件大小展示字符',
                             `file_type`        tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '文件类型',
                             `create_user`      bigint                                                  NOT NULL COMMENT '创建人',
                             `gmt_create`       datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `gmt_modified`     datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                             `update_time`      datetime     GENERATED ALWAYS AS (`gmt_modified`) STORED COMMENT '兼容旧Mapper字段，等同gmt_modified',
                             `update_user`      bigint                                                  NOT NULL COMMENT '更新人',
                             `deleted`          int                                                     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                             `lock_version`     int                                                     NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `index_file_list` (`user_id`, `deleted`, `parent_id`, `file_type`, `id`, `filename`, `folder_flag`, `file_size_desc`, `gmt_create`, `gmt_modified`) USING BTREE COMMENT '查询文件列表索引',
                             KEY `idx_user_folder` (`user_id`, `deleted`, `folder_flag`, `parent_id`) USING BTREE,
                             KEY `idx_parent_deleted` (`parent_id`, `deleted`) USING BTREE,
                             KEY `idx_user_fileid` (`user_id`, `file_id`) USING BTREE
)   ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_bin COMMENT='用户文件信息表'
    ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for file_chunk
-- ----------------------------
DROP TABLE IF EXISTS `file_chunk`;
CREATE TABLE `file_chunk` (
                              `id`              bigint                                                  NOT NULL COMMENT '主键',
                              `identifier`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '文件唯一标识',
                              `real_path`       varchar(700) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '分片存储路径',
                              `chunk_number`    int                                                     NOT NULL DEFAULT 0 COMMENT '分片编号',
                              `expiration_time` datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
                              `create_user`     bigint                                                  NOT NULL COMMENT '创建人',
                              `update_user`     bigint                                                  NOT NULL DEFAULT 0 COMMENT '更新人',
                              `gmt_create`      datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `gmt_modified`    datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                              `deleted`         int                                                     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                              `lock_version`    int                                                     NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `uk_identifier_chunk_number_create_user` (`identifier`, `chunk_number`, `create_user`) USING BTREE COMMENT '文件唯一标识、分片编号、用户ID唯一',
                              KEY `idx_chunk_expiration` (`expiration_time`) USING BTREE
)   ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_bin COMMENT='文件分片信息表'
    ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
                         `id`               bigint                                                  NOT NULL COMMENT '分享id',
                         `share_name`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '分享名称',
                         `share_type`       tinyint                                                 NOT NULL DEFAULT 0 COMMENT '分享类型（0有提取码）',
                         `share_day_type`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   NOT NULL DEFAULT '' COMMENT '分享天数类型（当前代码使用字符串）',
                         `share_day`        int                                                     NOT NULL DEFAULT 0 COMMENT '分享有效天数',
                         `share_end_time`   datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享结束时间',
                         `share_url`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '分享链接地址',
                         `share_code`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '分享提取码',
                         `share_status`     tinyint                                                 NOT NULL DEFAULT 0 COMMENT '分享状态（0正常；1有文件被删除）',
                         `create_user`      bigint                                                  NOT NULL COMMENT '分享创建人',
                         `gmt_create`       datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `gmt_modified`     datetime                                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                         `deleted`          int                                                     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                         `lock_version`     int                                                     NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE KEY `uk_share_url` (`share_url`) USING BTREE,
                         KEY `idx_share_create_user` (`create_user`, `gmt_create`) USING BTREE,
                         KEY `idx_share_status_end_time` (`share_status`, `share_end_time`) USING BTREE
)   ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_bin COMMENT='用户分享表'
    ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for share_file
-- ----------------------------
DROP TABLE IF EXISTS `share_file`;
CREATE TABLE `share_file` (
                              `id` bigint NOT NULL COMMENT '主键',
                              `share_id` bigint NOT NULL COMMENT '分享id',
                              `file_id` bigint NOT NULL COMMENT '文件记录ID（user_file.id）',
                              `create_user` bigint NOT NULL COMMENT '分享创建人',
                              `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                              `deleted` int NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                              `lock_version` int NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `uk_share_id_file_id` (`share_id`, `file_id`) USING BTREE COMMENT '分享ID、文件ID联合唯一',
                              KEY `idx_share_id_deleted` (`share_id`, `deleted`) USING BTREE,
                              KEY `idx_file_id` (`file_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户分享文件表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for user_search_history
-- ----------------------------
DROP TABLE IF EXISTS `user_search_history`;
CREATE TABLE `user_search_history` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `user_id` bigint NOT NULL COMMENT '用户id',
                                       `search_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '搜索文案',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                       `deleted` int NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                                       `lock_version` int NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `uk_user_id_search_content` (`user_id`, `search_content`) USING BTREE COMMENT '用户id+搜索内容唯一',
                                       KEY `idx_user_modified` (`user_id`, `gmt_modified`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户搜索历史表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for error_log
-- ----------------------------
DROP TABLE IF EXISTS `error_log`;
CREATE TABLE `error_log` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `log_content` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '日志内容',
                             `log_status` tinyint NOT NULL DEFAULT 0 COMMENT '日志状态：0未处理 1已处理',
                             `create_user` bigint NOT NULL COMMENT '创建人',
                             `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                             `update_user` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
                             `deleted` int NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，非0已删除',
                             `lock_version` int NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_log_status_deleted` (`log_status`, `deleted`, `gmt_create`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='错误日志表' ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;