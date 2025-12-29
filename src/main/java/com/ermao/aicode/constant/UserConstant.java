package com.ermao.aicode.constant;

/**
 * 用户常量
 * @author 21195
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 用户默认头像
     */
    String DEFAULT_USER_AVATAR = "https://ermao-1325310617.cos.ap-chengdu.myqcloud.com/AI/avatar/default-avatar.jpg";

    /**
     * 用户默认性别（2 保密）
     */
    Integer DEFAULT_USER_GENDER = 2;
}
