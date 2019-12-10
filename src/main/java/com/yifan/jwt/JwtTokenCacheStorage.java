package com.yifan.jwt;

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

    @Override
    public JwtTokenPair put(JwtTokenPair jwtTokenPair, String userId) {
        return jwtTokenPair;
    }

    @Override
    public void expire(String userId) {
    }

    @Override
    public JwtTokenPair get(String userId) {
        return null;
    }
}
