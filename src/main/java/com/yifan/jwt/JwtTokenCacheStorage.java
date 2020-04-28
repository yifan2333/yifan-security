package com.yifan.jwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jwt token 缓存
 *
 * @author wuyifan
 * @since 13 :28  2018/9/21
 */
public class JwtTokenCacheStorage implements JwtTokenStorage {

    private static final String TOKEN_CACHE = "usrTkn";

    private static final Map<String, JwtTokenPair> MAP = new ConcurrentHashMap<>();

    @Override
    public JwtTokenPair put(JwtTokenPair jwtTokenPair, String userId) {
        MAP.put(userId, jwtTokenPair);
        return jwtTokenPair;
    }

    @Override
    public void expire(String userId) {
        MAP.remove(userId);
    }

    @Override
    public JwtTokenPair get(String userId) {
        return MAP.get(userId);
    }
}
