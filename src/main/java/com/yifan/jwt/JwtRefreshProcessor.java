package com.yifan.jwt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月11日 上午11:26
 * @version 1.0
 */
public class JwtRefreshProcessor {

    private JwtTokenGenerator jwtTokenGenerator;
    private JwtTokenStorage jwtTokenStorage;
    private UserDetailsManager userDetailsManager;

    public JwtRefreshProcessor(JwtTokenGenerator jwtTokenGenerator, JwtTokenStorage jwtTokenStorage, UserDetailsManager userDetailsManager) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenStorage = jwtTokenStorage;
        this.userDetailsManager = userDetailsManager;
    }

    public JwtTokenPair refresh(String userId) {
        JwtTokenPair old = jwtTokenStorage.get(userId);

        if (old == null) {
            return null;
        }

        JSONObject object = jwtTokenGenerator.decodeAndVerify(old.getRefreshToken());

        String aud = object.getString("aud");
        String roles = object.getString("roles");
        Set<String> set = new HashSet<>(JSONArray.parseArray(roles, String.class));
        String add = object.getString("additional");
        Map<String, String> map = JSONObject.parseObject(add, Map.class);
        JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair(aud, set, map);


//        Authentication authentication = getAuthentication(aud);
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(authentication);
//        SecurityContextHolder.setContext(context);
        return jwtTokenPair;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String aud) {
        UserDetails user = userDetailsManager.loadUserByUsername(aud);
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
