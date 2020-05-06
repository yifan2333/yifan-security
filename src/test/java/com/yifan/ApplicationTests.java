package com.yifan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yifan.jwt.JwtPayload;
import com.yifan.jwt.JwtRefreshProcessor;
import com.yifan.jwt.JwtTokenCacheStorage;
import com.yifan.jwt.JwtTokenGenerator;
import com.yifan.jwt.JwtTokenPair;
import com.yifan.jwt.JwtTokenStorage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	/**
	 * 生成Jwt
	 */
	@Resource
	private JwtTokenGenerator jwtTokenGenerator;

	/**
	 * 缓存Jwt
	 */
	@Resource
	private JwtTokenStorage jwtTokenStorage;

	/**
	 * 刷新jwt
	 */
	@Resource
	private JwtRefreshProcessor jwtRefreshProcessor;

	@Test
	public void contextLoads() {
	}


	@Test
	public void jwtTest() {
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		Map<String, String> map = new HashMap<>();
		map.put("phone", "1234567890");

		// 生成jwt
		JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair("wuyifanc", roles, map);

		System.out.println("生成的jwt:\n" + JSONObject.toJSONString(jwtTokenPair));

		// 缓存中取jwt
		JwtTokenPair cache = jwtTokenStorage.get("wuyifanc");

		assert jwtTokenPair.equals(cache);

		// 解析jwt
		JwtPayload jwtPayload = jwtTokenGenerator.decodeAndVerify(jwtTokenPair.getAccessToken());

		System.out.println("解析jwt:\n" + JSONObject.toJSONString(jwtPayload));

		// 刷新jwt
		JwtTokenPair refresh = jwtRefreshProcessor.refresh("wuyifanc");

		System.out.println("刷新的jwt:\n" + JSONObject.toJSONString(refresh));
	}


	@Test
	public void passwordEncoderTest() {
		PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		System.out.println(delegatingPasswordEncoder.encode("wuyifanc"));

		PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

		System.out.println(bCryptPasswordEncoder.encode("wuyifanc"));
	}
}
