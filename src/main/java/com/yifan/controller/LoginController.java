package com.yifan.controller;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yifan.entity.ActionResult;
import com.yifan.entity.ResultType;
import com.yifan.entity.SysUser;
import com.yifan.service.UserDetailsService;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月06日 上午10:26
 * @version 1.0
 */
@RestController
@RequestMapping("login")
public class LoginController {
    @Resource
    private UserDetailsService sysUserService;

    /**
     * 登录失败返回 401 以及提示信息.
     *
     * @return the rest
     */
    @PostMapping("/failure")
    public ActionResult<String> loginFailure() {

        return new ActionResult.Builder<String>()
                .resultType(ResultType.FAILURE).message("登录失败")
                .build();
    }

    /**
     * 登录成功后拿到个人信息.
     *
     * @return the rest
     */
    @PostMapping("/success")
    public ActionResult<SysUser> loginSuccess() {
        // 登录成功后用户的认证信息 UserDetails会存在 安全上下文寄存器 SecurityContextHolder 中
        Authentication currentUser =  SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        SysUser sysUser = sysUserService.queryByUsername(username);
        // 脱敏
        sysUser.setPassword("[PROTECT]");
        return new ActionResult.Builder<SysUser>().data(sysUser).message("登录成功").build();
    }
}
