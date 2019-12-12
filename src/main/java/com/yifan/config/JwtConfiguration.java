package com.yifan.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import com.yifan.entity.ActionResult;
import com.yifan.entity.TokenResultDto;
import com.yifan.jwt.JwtProperties;
import com.yifan.jwt.JwtRefreshProcessor;
import com.yifan.jwt.JwtTokenCacheStorage;
import com.yifan.jwt.JwtTokenGenerator;
import com.yifan.jwt.JwtTokenPair;
import com.yifan.jwt.JwtTokenStorage;
import com.yifan.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "jwt.config", name = "enabled")
@Configuration
public class JwtConfiguration {

    /**
     * token缓存器
     * @return com.yifan.jwt.JwtTokenStorage
     * @author wuyifan
     * @date 2019年12月12日 上午11:46
     */
    @Bean
    public JwtTokenStorage jwtTokenStorage() {
        return new JwtTokenCacheStorage();
    }
    /**
     * token生成器
     * @param jwtTokenStorage jwtTokenStorage
     * @param jwtProperties jwtProperties
     * @return com.yifan.jwt.JwtTokenGenerator
     * @author wuyifan
     * @date 2019年12月12日 上午11:46
     */
    @Bean
    public JwtTokenGenerator jwtTokenGenerator(JwtTokenStorage jwtTokenStorage, JwtProperties jwtProperties) {
        return new JwtTokenGenerator(jwtTokenStorage, jwtProperties);
    }

    /**
     * token刷新器
     * @param jwtTokenGenerator jwtTokenGenerator
     * @param jwtTokenStorage jwtTokenStorage
     * @return com.yifan.jwt.JwtRefreshProcessor
     * @author wuyifan
     * @date 2019年12月12日 上午11:46
     */
    @Bean
    public JwtRefreshProcessor jwtRefreshProcessor(JwtTokenGenerator jwtTokenGenerator, JwtTokenStorage jwtTokenStorage) {
        return new JwtRefreshProcessor(jwtTokenGenerator, jwtTokenStorage);
    }

    /**
     * 处理登录成功后返回 JWT Token 对.
     * @param jwtTokenGenerator jwtTokenGenerator
     * @return org.springframework.security.web.authentication.AuthenticationSuccessHandler
     * @author wuyifan
     * @date 2019年12月12日 上午11:46
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        return (request, response, authentication) -> {
            if (response.isCommitted()) {
                log.debug("Response has already been committed");
                return;
            }
            TokenResultDto dto = new TokenResultDto();
            dto.setFlag("success_login");
            String username = authentication.getName();
            Collection<?> authorities = authentication.getAuthorities();
            Set<String> roles = new HashSet<>();
            if (!CollectionUtils.isEmpty(authorities)) {
                for (Object obj : authorities) {
                    GrantedAuthority authority = (GrantedAuthority)obj;
                    String roleName = authority.getAuthority();
                    roles.add(roleName);
                }
            }

            JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair(username, roles, null);
            dto.setAccess_token(jwtTokenPair.getAccessToken());
            dto.setRefresh_token(jwtTokenPair.getRefreshToken());
            response.setStatus(HttpServletResponse.SC_OK);
            ResponseUtils.responseJsonWriter(response, new ActionResult.Builder<TokenResultDto>()
                    .data(dto)
                    .message("登陆成功")
                    .build());
        };
    }

    /**
     * 失败登录处理器 处理登录失败后的逻辑 登录失败返回信息 以此为依据跳转
     * @return org.springframework.security.web.authentication.AuthenticationFailureHandler
     * @author wuyifan
     * @date 2019年12月12日 上午11:47
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            if (response.isCommitted()) {
                log.debug("Response has already been committed");
                return;
            }
            TokenResultDto dto = new TokenResultDto();
            dto.setFlag("failure_login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseUtils.responseJsonWriter(response, new ActionResult.Builder<TokenResultDto>()
                    .data(dto)
                    .code(401)
                    .message("认证失败")
                    .build());
        };
    }

}
