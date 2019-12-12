package com.yifan.jwt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;

import lombok.Data;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月12日 上午11:12
 * @version 1.0
 */
@Data
public class JwtPayload {

    /**
     * 附加的属性
     */
    private Map<String, String> additional;
    /**
     * jwt签发者
     **/
    private String iss;
    /**
     * jwt所面向的用户
     **/
    private String sub;
    /**
     * 接收jwt的一方
     **/
    private String aud;
    /**
     * jwt的过期时间，这个过期时间必须要大于签发时间
     **/
    private String exp;
    /**
     * jwt的签发时间
     **/
    private String iat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    /**
     * 权限集
     */
    private String roles;
    /**
     * jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击
     **/
    private String jti = UUID.randomUUID().toString();


    public Set<String> roles() {
        return new HashSet<>(JSONArray.parseArray(this.roles, String.class));
    }
}
