package com.ermao.aicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 21195
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户性别
     */
    private Integer userGender;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 邮箱
     */
    private String userEmail;

    @Serial
    private static final long serialVersionUID = 1L;
}
