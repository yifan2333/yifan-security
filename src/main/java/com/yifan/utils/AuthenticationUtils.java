package com.yifan.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.yifan.jwt.JwtPayload;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月11日 下午4:54
 * @version 1.0
 */
public class AuthenticationUtils {

    /**
     * 将认证的结果保存到 安全上下文中
     * @param request request
     * @param jwtPayload jwtPayload
     * @author wuyifan
     * @date 2019年12月12日 上午11:43
     */
    public static void authSuccess(HttpServletRequest request, JwtPayload jwtPayload) {
        // 解析 权限集合  这里
        String roles = jwtPayload.getRoles();
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
        User user = new User(jwtPayload.getAud(), "[PROTECTED]", authorities);
        // 构建用户认证token
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 放入安全上下文中
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
