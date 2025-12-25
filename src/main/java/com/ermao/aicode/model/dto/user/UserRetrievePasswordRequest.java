package com.ermao.aicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 21195
 */
@Data
public class UserRetrievePasswordRequest implements Serializable {

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    private Integer code;

    /**
     * 新密码
     */
    private String password;

    /**
     * 校验密码
     */
    private String checkPassword;

    @Serial
    private static final long serialVersionUID = 1L;

}
