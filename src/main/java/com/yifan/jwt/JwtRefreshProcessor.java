package com.yifan.jwt;

/** 
 * JWT 刷新类
 *
 * @author: wuyifan
 * @since: 2019年12月11日 上午11:26
 * @version 1.0
 */
public class JwtRefreshProcessor {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenStorage jwtTokenStorage;

    public JwtRefreshProcessor(JwtTokenGenerator jwtTokenGenerator, JwtTokenStorage jwtTokenStorage) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenStorage = jwtTokenStorage;
    }

    public JwtTokenPair refresh(String userId) {
        JwtTokenPair old = jwtTokenStorage.get(userId);

        if (old == null) {
            return null;
        }
        // 解析refresh_token获取用户信息
        JwtPayload jwtPayload = jwtTokenGenerator.decodeAndVerify(old.getRefreshToken());
        // 根据用户信息重新生成token,并保存到缓存中
        return jwtTokenGenerator.jwtTokenPair(jwtPayload);
    }
}
