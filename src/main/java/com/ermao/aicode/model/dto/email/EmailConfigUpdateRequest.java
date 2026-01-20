package com.ermao.aicode.model.dto.email;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮件服务更改请求
 * @author 21195
 */
@Data
public class EmailConfigUpdateRequest implements Serializable {
    private String host;
    private Integer port;
    private String user;
    private String pass;
    private String fromEmail;
    private Integer sslEnable;
    @Serial
    private static final long serialVersionUID = 1L;
}

