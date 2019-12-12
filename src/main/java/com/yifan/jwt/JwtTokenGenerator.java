package com.yifan.jwt;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * JwtTokenGenerator
 *
 * @author wuyifan
 * @since 15 :26 2019/10/25
 */
@Slf4j
public class JwtTokenGenerator {
    private JwtProperties jwtProperties;
    private JwtTokenStorage jwtTokenStorage;
    private KeyPair keyPair;

    /**
     * Instantiates a new Jwt token generator.
     *
     * @param jwtTokenStorage the jwt token storage
     * @param jwtProperties   the jwt properties
     */
    public JwtTokenGenerator(JwtTokenStorage jwtTokenStorage, JwtProperties jwtProperties) {
        this.jwtTokenStorage = jwtTokenStorage;
        this.jwtProperties = jwtProperties;

        KeyPairFactory keyPairFactory = new KeyPairFactory();
        this.keyPair = keyPairFactory.create(jwtProperties.getKeyLocation(), jwtProperties.getKeyAlias(), jwtProperties.getKeyPass());
    }

    public JwtTokenPair jwtTokenPair(JwtPayload jwtPayload) {
        return jwtTokenPair(jwtPayload.getAud(), jwtPayload.roles(), jwtPayload.getAdditional());
    }
    /**
     * Jwt token pair jwt token pair.
     *
     * @param aud        the aud
     * @param roles      the roles
     * @param additional the additional
     * @return the jwt token pair
     */
    public JwtTokenPair jwtTokenPair(String aud, Set<String> roles, Map<String, String> additional) {
        String accessToken = jwtToken(aud, jwtProperties.getAccessExpDays(), roles, additional);
        String refreshToken = jwtToken(aud, jwtProperties.getRefreshExpDays(), roles, additional);

        JwtTokenPair jwtTokenPair = new JwtTokenPair();
        jwtTokenPair.setAccessToken(accessToken);
        jwtTokenPair.setRefreshToken(refreshToken);
        // 放入缓存
        jwtTokenStorage.put(jwtTokenPair, aud);
        return jwtTokenPair;
    }


    /**
     * Jwt token string.
     *
     * @param aud        the aud
     * @param exp        the exp
     * @param roles      the roles
     * @param additional the additional
     * @return the string
     */
    private String jwtToken(String aud, int exp, Set<String> roles, Map<String, String> additional) {
        String payload = new JwtPayloadBuilder()
                .iss(jwtProperties.getIss())
                .sub(jwtProperties.getSub())
                .aud(aud)
                .additional(additional)
                .roles(roles)
                .expDays(exp)
                .builder();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RsaSigner signer = new RsaSigner(privateKey);
        return JwtHelper.encode(payload, signer).getEncoded();
    }


    /**
     * 解码 并校验签名 过期不予解析
     *
     * @param jwtToken the jwt token
     * @return the jwt claims
     */
    public JwtPayload decodeAndVerify(String jwtToken) {
        Assert.hasText(jwtToken, "jwt token must not be bank");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) this.keyPair.getPublic();
        SignatureVerifier rsaVerifier = new RsaVerifier(rsaPublicKey);
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, rsaVerifier);
        String claims = jwt.getClaims();
        JwtPayload jwtPayload = JSONObject.parseObject(claims, JwtPayload.class);
        String exp = jwtPayload.getExp();

        if (isExpired(exp)) {
            throw new IllegalStateException("jwt token is expired");
        }
        return jwtPayload;
    }

    /**
     * 判断jwt token是否过期.
     *
     * @param exp the jwt token exp
     * @return the boolean
     */
    private boolean isExpired(String exp) {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(exp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
