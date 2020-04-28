package com.yifan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yifan.jwt.JwtRefreshProcessor;
import com.yifan.jwt.JwtTokenCacheStorage;
import com.yifan.jwt.JwtTokenGenerator;
import com.yifan.jwt.JwtTokenPair;
import com.yifan.jwt.JwtTokenStorage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Resource
	private JwtTokenGenerator jwtTokenGenerator;

	@Test
	public void contextLoads() {
	}


	@Test
	public void jwtTest() {
		Set<String> roles = new HashSet<>();
		roles.add("ROLE_ADMIN");
		Map<String, String> map = new HashMap<>();
		map.put("phone", "1234567890");
		JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair("wuyifanc", roles, map);

		System.out.println(JSONObject.toJSONString(jwtTokenPair));
	}


}
