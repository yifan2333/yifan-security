package com.yifan.jwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Jwt token cache storage.
 *
 * @author wuyifan
 * @since 13 :28  2018/9/21
 */
public class JwtTokenCacheStorage implements JwtTokenStorage {
    /**
     * 查看缓存配置文件 ehcache.xml 定义 过期时间与 refresh token 过期一致.
     */
    private static final String TOKEN_CACHE = "usrTkn";

    private static Map<String, JwtTokenPair> MAP = new ConcurrentHashMap<>();

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
