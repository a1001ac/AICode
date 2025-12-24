package com.ermao.aicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 21195
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}
