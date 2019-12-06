package com.yifan.config;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yifan.filter.JsonLoginPostProcessor;
import com.yifan.filter.LoginPostProcessor;
import com.yifan.filter.PreLoginFilter;
import com.yifan.handler.CustomLogoutHandler;
import com.yifan.handler.CustomLogoutSuccessHandler;
import com.yifan.service.UserDetailsService;


@Configuration
@EnableWebSecurity
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CustomSpringBootWebSecurityConfiguration {

    public static final String LOGIN_PROCESSING_URL = "/process";

    /**
     * 配置json登录
     * @return com.yifan.filter.JsonLoginPostProcessor
     * @author wuyifan
     * @date 2019年12月6日 下午2:27
     */
    @Bean
    public JsonLoginPostProcessor jsonLoginPostProcessor(){
        return new JsonLoginPostProcessor();
    }

    /**
     * 配置登录拦截器,用于选择是哪一种登录方式
     * @param loginPostProcessors loginPostProcessors
     * @return com.yifan.filter.PreLoginFilter
     * @author wuyifan
     * @date 2019年12月6日 下午2:28
     */
    @Bean
    public PreLoginFilter preLoginFilter(Collection<LoginPostProcessor> loginPostProcessors) {
        return new PreLoginFilter(LOGIN_PROCESSING_URL, loginPostProcessors);
    }

    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    static class  DefaultConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Resource
        private PreLoginFilter preLoginFilter;

        /**
         * 用来配置认证管理器 AuthenticationManager
         * 所有的 UserDetails 相关的配置,包含 PasswordEncoder
         * @param auth auth
         * @author wuyifan
         * @date 2019年12月5日 下午3:41
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
        }

        /**
         * 核心过滤器配置方法  用来配置 WebSecurity
         * 而 WebSecurity 是基于 Servlet Filter 用来配置 springSecurityFilterChain 。
         * 而 springSecurityFilterChain 又被委托给了 Spring Security 核心过滤器 Bean DelegatingFilterProxy 。
         * 相关逻辑你可以在 WebSecurityConfiguration 中找到。
         * 我们一般不会过多来自定义 WebSecurity , 使用较多的使其ignoring() 方法用来忽略 Spring Security 对静态资源的控制。
         * @param web web
         * @author wuyifan
         * @date 2019年12月5日 下午3:43
         */
        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);
        }

        /**
         * 用来配置 HttpSecurity 。
         * HttpSecurity 用于构建一个安全过滤器链 SecurityFilterChain 。
         * SecurityFilterChain 最终被注入核心过滤器 。
         * HttpSecurity 有许多我们需要的配置。我们可以通过它来进行自定义安全访问策略。
         * @param http http
         * @author wuyifan
         * @date 2019年12月5日 下午3:45
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .cors()
                    .and()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .addFilterBefore(preLoginFilter, UsernamePasswordAuthenticationFilter.class)
                    .formLogin()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successForwardUrl("/login/success")
                    .failureForwardUrl("/login/failure")
                    .and()
                    .logout()
                    .addLogoutHandler(new CustomLogoutHandler())
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler());
        }
    }

    /**
     * 自定义UserDetailsManager, 从数据库中获取用户信息
     * @param userDetailsRepository userDetailsRepository
     * @return org.springframework.security.provisioning.UserDetailsManager
     * @author wuyifan
     * @date 2019年12月6日 下午2:28
     */
    @Bean
    public UserDetailsManager userDetailsManager(UserDetailsService userDetailsRepository) {
        return new UserDetailsManager() {
            @Override
            public void createUser(UserDetails user) {
                userDetailsRepository.createUser(user);
            }

            @Override
            public void updateUser(UserDetails user) {
                userDetailsRepository.updateUser(user);
            }

            @Override
            public void deleteUser(String username) {
                userDetailsRepository.deleteUser(username);
            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {
                userDetailsRepository.changePassword(oldPassword, newPassword);
            }

            @Override
            public boolean userExists(String username) {
                return userDetailsRepository.userExists(username);
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDetailsRepository.loadUserByUsername(username);
            }
        };
    }
}

