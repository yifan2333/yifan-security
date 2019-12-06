package com.yifan.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.extern.slf4j.Slf4j;

/** 
 * 自定义用户退出登录的方法
 * 可以实现退出登录的相关逻辑
 *
 * @author: wuyifan
 * @since: 2019年12月06日 下午3:08
 * @version 1.0
 */
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String name = authentication.getName();
        log.info("username: {}  is offline now", name);
    }
}
