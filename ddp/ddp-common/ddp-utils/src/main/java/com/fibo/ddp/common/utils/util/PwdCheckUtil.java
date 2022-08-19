package com.fibo.ddp.common.utils.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 密码校验
 * @createDate 2022-08-19 15:55:45
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
public class PwdCheckUtil {


    /**
     * 系统默认密码
     */
    public static final String SYS_DEFAULT_PASSWORD = "engine888!";

    /**
     * 密码复杂度校验-正则表达式
     * 密码必须包含字母、数字和特殊字符，且长度是8至16位
     */
    public static final String USER_LOGIN_REGEX="^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[`~!@#$%^&*()-=_+;':\",./<>?])(?=\\S+$).{8,16}$";

    /**
     * 密码复杂度校验，判断有效性
     * @param password 密码信息
     * @return 校验密码是否合规有效
     */
    public static boolean isValidPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        return password.matches(USER_LOGIN_REGEX);
    }
}
