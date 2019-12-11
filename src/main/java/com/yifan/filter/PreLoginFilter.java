package com.yifan.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.yifan.enums.LoginTypeEnum;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;


/** 
 * 根据请求的 login_type判断是哪一种登录方式\
 * 解析将username和password放入请求中
 *
 * @author: wuyifan
 * @since: 2019年12月06日 上午11:10
 * @version 1.0
 */
@Slf4j
public class PreLoginFilter extends GenericFilterBean {
    private static final String LOGIN_TYPE_KEY = "login_type";


    private RequestMatcher requiresAuthenticationRequestMatcher;
    private Map<LoginTypeEnum, LoginPostProcessor> processors = new HashMap<>();


    public PreLoginFilter(String loginProcessUrl, Collection<LoginPostProcessor> loginPostProcessors) {
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(loginProcessUrl, "POST");
        LoginPostProcessor loginPostProcessor = defaultLoginPostProcessor();
        processors.put(loginPostProcessor.getLoginTypeEnum(), loginPostProcessor);

        if (!CollectionUtils.isEmpty(loginPostProcessors)) {
            loginPostProcessors.forEach(element -> processors.put(element.getLoginTypeEnum(), element));
        }
    }


    private LoginTypeEnum getTypeFromReq(ServletRequest request) {
        String parameter = request.getParameter(LOGIN_TYPE_KEY);

        int i = StringUtils.isEmpty(parameter) ? 0 : Integer.parseInt(parameter);
        LoginTypeEnum[] values = LoginTypeEnum.values();
        return values[i];
    }


    /**
     * 默认还是Form
     *
     * @return the login post processor
     */
    private LoginPostProcessor defaultLoginPostProcessor() {
        return new LoginPostProcessor() {

            @Override
            public LoginTypeEnum getLoginTypeEnum() {
                return LoginTypeEnum.FORM;
            }

            @Override
            public String obtainUsername(ServletRequest request) {
                return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
            }

            @Override
            public String obtainPassword(ServletRequest request) {
                return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
            }
        };
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("url {}", ((HttpServletRequest) request).getRequestURI());
        ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper((HttpServletRequest) request);
        if (requiresAuthenticationRequestMatcher.matches((HttpServletRequest) request)) {
            LoginTypeEnum typeFromReq = getTypeFromReq(request);
            LoginPostProcessor loginPostProcessor = processors.get(typeFromReq);
            String username = loginPostProcessor.obtainUsername(request);
            String password = loginPostProcessor.obtainPassword(request);
            parameterRequestWrapper.setAttribute(SPRING_SECURITY_FORM_USERNAME_KEY, username);
            parameterRequestWrapper.setAttribute(SPRING_SECURITY_FORM_PASSWORD_KEY, password);
        }
        chain.doFilter(parameterRequestWrapper, response);
    }
}
