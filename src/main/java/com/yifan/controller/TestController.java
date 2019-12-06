package com.yifan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年11月18日 下午2:34
 * @version 1.0
 */
@Controller
public class TestController {

    @GetMapping("test")
    @ResponseBody
    public String test() {
        return "test";
    }
}
