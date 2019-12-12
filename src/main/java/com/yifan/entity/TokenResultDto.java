package com.yifan.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月12日 上午11:31
 * @version 1.0
 */
@Data
public class TokenResultDto {
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private String flag;

    private String access_token;

    private String refresh_token;
}
