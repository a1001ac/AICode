package com.ermao.aicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员修改用户角色请求
 * @author 21195
 */
@Data
public class UserUpdateRoleRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户角色 (user / admin)
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

