package com.yifan.filter;

import javax.servlet.ServletRequest;

import com.yifan.enums.LoginTypeEnum;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月06日 上午11:09
 * @version 1.0
 */
public interface LoginPostProcessor {
    /**
     * 获取 登录类型
     *
     * @return the type
     */
    LoginTypeEnum getLoginTypeEnum();

    /**
     * 获取用户名
     *
     * @param request the request
     * @return the string
     */
    String obtainUsername(ServletRequest request);

    /**
     * 获取密码
     *
     * @param request the request
     * @return the string
     */
    String obtainPassword(ServletRequest request);
}
