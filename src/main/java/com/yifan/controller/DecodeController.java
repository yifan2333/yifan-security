package com.yifan.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yifan.entity.ActionResult;
import com.yifan.jwt.JwtPayload;
import com.yifan.jwt.JwtTokenGenerator;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月10日 下午2:18
 * @version 1.0
 */
@RestController
public class DecodeController {

    @Resource
    private JwtTokenGenerator jwtTokenGenerator;

    @GetMapping("decode")
    public ActionResult<JwtPayload> decode(@RequestParam String jwtToken) {
        JwtPayload object = jwtTokenGenerator.decodeAndVerify(jwtToken);

        return new ActionResult.Builder<JwtPayload>().data(object).build();
    }

}
