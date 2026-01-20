package com.ermao.aicode.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 邮件配置
 * @author 21195
 */
@TableName(value ="email_config")
@Data
public class EmailConfig implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 邮箱服务器host
     */
    private String host;

    /**
     * 邮箱服务器端口
     */
    private Integer port;

    /**
     * 发件人
     */
    private String user;

    /**
     * 发件人邮箱密码
     */
    private String pass;

    /**
     * 发件人邮箱
     */
    private String fromEmail;

    /**
     * 是否启用ssl 0否 1是
     */
    private Integer sslEnable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

