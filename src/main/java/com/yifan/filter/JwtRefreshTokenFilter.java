package com.yifan.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yifan.entity.ActionResult;
import com.yifan.entity.TokenResultDto;
import com.yifan.entrypoint.SimpleAuthenticationEntryPoint;
import com.yifan.jwt.JwtPayload;
import com.yifan.jwt.JwtRefreshProcessor;
import com.yifan.jwt.JwtTokenGenerator;
import com.yifan.jwt.JwtTokenPair;
import com.yifan.jwt.JwtTokenStorage;
import com.yifan.utils.AuthenticationUtils;
import com.yifan.utils.ResponseUtils;

import lombok.extern.slf4j.Slf4j;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月11日 上午10:52
 * @version 1.0
 */
@Slf4j
public class JwtRefreshTokenFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_PREFIX = "Bearer ";

    private static final String REFRESH_TOKEN_PROCESSING_URL = "/refresh_token";

    /**
     * 认证如果失败由该端点进行响应
     */
    private AuthenticationEntryPoint authenticationEntryPoint = new SimpleAuthenticationEntryPoint();
    private JwtRefreshProcessor jwtRefreshProcessor;
    private JwtTokenGenerator jwtTokenGenerator;
    private RequestMatcher requiresAuthenticationRequestMatcher;
    private JwtTokenStorage jwtTokenStorage;

    public JwtRefreshTokenFilter(JwtRefreshProcessor jwtRefreshProcessor,
                                 JwtTokenGenerator jwtTokenGenerator,
                                 JwtTokenStorage jwtTokenStorage) {
        this.jwtRefreshProcessor = jwtRefreshProcessor;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenStorage = jwtTokenStorage;
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(REFRESH_TOKEN_PROCESSING_URL, "POST");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("url {}", request.getRequestURI());
        if (requiresAuthenticationRequestMatcher.matches(request)) {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(header) && header.startsWith(AUTHENTICATION_PREFIX)) {
                String jwtToken = header.replace(AUTHENTICATION_PREFIX, "");
                if (StringUtils.hasText(jwtToken)) {
                    try {
                        refreshToken(jwtToken, response, request);
                        return;
                    } catch (AuthenticationException e) {
                        authenticationEntryPoint.commence(request, response, e);
                    }
                } else {
                    // 带安全头 没有带token
                    authenticationEntryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException("token is not found"));
                }

            }
        }
        filterChain.doFilter(request, response);
    }

    private void refreshToken(String jwtToken, HttpServletResponse response, HttpServletRequest request)
            throws AuthenticationException, IOException {

        JwtPayload jwtPayload = jwtTokenGenerator.decodeAndVerify(jwtToken);

        if (Objects.nonNull(jwtPayload)) {
            String username = jwtPayload.getAud();

            JwtTokenPair tokenPair = jwtTokenStorage.get(username);
            if (!jwtToken.equals(tokenPair.getRefreshToken())) {
                // token 不匹配
                log.debug("refresh token : {}  is  not in matched", jwtToken);
                throw new BadCredentialsException("refresh token is not matched");
            }
            JwtTokenPair jwtTokenPair = jwtRefreshProcessor.refresh(username);
            TokenResultDto dto = new TokenResultDto();
            dto.setFlag("success_login");
            dto.setAccess_token(jwtTokenPair.getAccessToken());
            dto.setRefresh_token(jwtTokenPair.getRefreshToken());
            AuthenticationUtils.authSuccess(request, jwtPayload);
            response.setStatus(HttpServletResponse.SC_OK);
            ResponseUtils.responseJsonWriter(response, new ActionResult.Builder<>().data(dto).build());
        } else {
            // token 不匹配
            log.debug("refresh_token : {}  is  not in matched", jwtToken);
            throw new BadCredentialsException("refresh_token is not matched");
        }
    }
}

