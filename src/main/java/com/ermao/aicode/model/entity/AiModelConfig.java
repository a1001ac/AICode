package com.ermao.aicode.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 21195
 */
@TableName(value = "ai_model_config")
@Data
public class AiModelConfig implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Constants for config keys
     */
    public static final String KEY_STREAMING = "streaming_chat_model";
    public static final String KEY_REASONING = "reasoning_streaming_chat_model";
    public static final String KEY_ROUTING = "routing_chat_model";

    /**
     * 配置键
     */
    private String configKey;
    /**
     * 基础URL
     */
    private String baseUrl;
    /**
     * API密钥
     */
    private String apiKey;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 最大Token数
     */
    private Integer maxTokens;
    /**
     * 温度参数
     */
    private Double temperature;
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

