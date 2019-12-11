package com.yifan.jwt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 构建 jwt payload
 *
 * @author wuyifan
 * @since 11:27 2019/10/25
 **/
public class JwtPayloadBuilder {

    private Map<String, String> payload = new HashMap<>();
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
    private LocalDateTime exp;
    /**
     * jwt的签发时间
     **/
    private LocalDateTime iat = LocalDateTime.now();
    /**
     * 权限集
     */
    private Set<String> roles = new HashSet<>();
    /**
     * jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击
     **/
    private String jti = UUID.randomUUID().toString();

    public JwtPayloadBuilder iss(String iss) {
        this.iss = iss;
        return this;
    }


    public JwtPayloadBuilder sub(String sub) {
        this.sub = sub;
        return this;
    }

    public JwtPayloadBuilder aud(String aud) {
        this.aud = aud;
        return this;
    }


    public JwtPayloadBuilder roles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public JwtPayloadBuilder expDays(int days) {
        Assert.isTrue(days > 0, "jwt expireDate must after now");
        this.exp = this.iat.plusDays(days);
        return this;
    }

    public JwtPayloadBuilder additional(Map<String, String> additional) {
        this.additional = additional;
        return this;
    }

    public String builder() {
        payload.put("iss", this.iss);
        payload.put("sub", this.sub);
        payload.put("aud", this.aud);
        payload.put("exp", this.exp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        payload.put("iat", this.iat.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        payload.put("jti", this.jti);
        System.out.println(jti);
        if (additional == null) {
            additional = new HashMap<>();
        }
        additional.put("photoId", "12345648955488");
        if (!CollectionUtils.isEmpty(additional)) {
            payload.put("additional", JSONObject.toJSONString(additional));
        }
        payload.put("roles", JSONObject.toJSONString(this.roles));
        return JSONObject.toJSONString(payload);
    }
}
