-- 用户表
create table user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userGender   tinyint                                null comment '用户性别 0-女 1-男 2-保密',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userEmail    varchar(256)                           null comment '邮箱',
    registerIp   varchar(128)                           null comment '注册IP',
    registerAddress varchar(256)                        null comment '注册地址',
    loginIp      varchar(128)                           null comment '登录IP',
    loginAddress varchar(256)                           null comment '登录地址',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 应用表
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    initPrompt   text                               null comment '应用初始化的 prompt',
    codeGenType  varchar(64)                        null comment '代码生成类型（枚举）',
    deployKey    varchar(64)                        null comment '部署标识',
    deployedTime datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    userId       bigint                             not null comment '创建用户id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deployKey), -- 确保部署标识唯一
    INDEX idx_appName (appName),         -- 提升基于应用名称的查询性能
    INDEX idx_userId (userId)            -- 提升基于用户 ID 的查询性能
) comment '应用' collate = utf8mb4_unicode_ci;

-- 对话历史表
create table chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    message     text                               not null comment '消息',
    messageType varchar(32)                        not null comment 'user/ai',
    appId       bigint                             not null comment '应用id',
    userId      bigint                             not null comment '创建用户id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_appId (appId),                       -- 提升基于应用的查询性能
    INDEX idx_createTime (createTime),             -- 提升基于时间的查询性能
    INDEX idx_appId_createTime (appId, createTime) -- 游标查询核心索引
) comment '对话历史' collate = utf8mb4_unicode_ci;

-- 每日访问统计表
create table view
(
    id         bigint auto_increment comment 'id' primary key,
    viewNum    bigint                             not null comment '访问量',
    dateStr    varchar(32)                        not null comment '统计日期 yyyy-MM-dd',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE KEY uk_dateStr (dateStr) -- 保证每天只有一条记录
) comment '每日访问统计' collate = utf8mb4_unicode_ci;

-- AI 模型配置表
create table ai_model_config
(
    id           bigint auto_increment comment 'id' primary key,
    configKey    varchar(64)                        not null comment '配置标识: streaming_chat_model, reasoning_streaming_chat_model, routing_chat_model',
    baseUrl      varchar(512)                       null comment '基础url',
    apiKey       varchar(512)                       null comment 'apikey',
    modelName    varchar(128)                       null comment '模型名称',
    maxTokens    int                                null comment '最大token数',
    temperature  double                             null comment '温度',
    maxRetries   int                                null comment '最大重试次数 (仅路由模型使用)',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_configKey (configKey)
) comment 'AI 模型配置' collate = utf8mb4_unicode_ci;

INSERT INTO ai_model_config
(id, configKey, isDelete)
VALUES
    (2004799122333110273, 'streaming_chat_model',0),
    (2004844942654873602, 'reasoning_streaming_chat_model',0),
    (2005881074582802433, 'routing_chat_model',0);

-- 邮件配置表
create table email_config
(
    id          bigint auto_increment comment 'id' primary key,
    host        varchar(128)                       null comment 'SMTP服务器域名',
    port        int                                null comment 'SMTP服务端口',
    user        varchar(128)                       null comment '发件人账号',
    pass        varchar(128)                       null comment '发件人密码/授权码',
    fromEmail   varchar(128)                       null comment '发件人邮箱',
    sslEnable   tinyint  default 1                 null comment '是否启用SSL 0-否 1-是',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '邮件配置' collate = utf8mb4_unicode_ci;

INSERT INTO email_config (id, host, port, user, pass, fromEmail, sslEnable)
VALUES (2004541730207023106, 'smtp.qq.com', 465, 'ermao', 'ejdoolxjvydaedjb', '2119527099@qq.com', 1);
