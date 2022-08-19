package com.fibo.ddp.common.utils.constant;

/**
 * @author lisw
 * @program fork-FiboRule
 * @description 关于验证TOKEN相关的常量
 * @createDate 2022-08-19 14:47:17
 * @slogan 长风破浪会有时，直挂云帆济沧海。
 **/
public class TokenConstants {

    // 前端传输在Header中的token名称
    public static final String HEADER_NAME_SYSTEM_KEY_TOKEN = "token";
    // token（有效期）时间 单位秒
    public static final Long LOGIN_TOKEN_TIME = 7200L;
    // token最大剩余时间，需刷新 单位秒
    public static final Long LOGIN_TOKEN_REFRESH_TIME = 600L;

    /**
     * 登录时
     * Redis中存储的TOKEN——KEY前缀，用于根据token获取用户信息
     * key------user:token:{{token}}
     * value------用户对象信息
     */
    public static final String USER_TOKEN_PREFX = "user:token:";


    /**
     * 用户当前有效的token信息，登录时
     * Redis中存储的TOKEN——KEY前缀，用户根据用户ID获取TOKEN
     * key------user:currentToken:{{token}}
     * value------{{token}}
     */
    public static final String USER_CURRENT_TOKEN_PREFX = "user:currentToken:";





}
