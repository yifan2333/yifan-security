package com.yifan.jwt;

import java.io.Serializable;

import lombok.Data;

/**
 * JwtTokenPair
 *
 * @author Felordcn
 * @since 16:09 2019/10/25
 **/
@Data
public class JwtTokenPair implements Serializable {
    private static final long serialVersionUID = -8518897818107784049L;
    private String accessToken;
    private String refreshToken;
}
