package com.yifan.entity;

/**
 * http 请求返回状态码
 * 
 * @author caowuchao
 * @since 2018年5月16日
 * @version 1.0
 */
public enum ResultType {

	OK(200, "成功"),

	FAILURE(62210, "请求失败"),

	TODO_FINISHED(62300, "待办已处理"),

	NO_TOKEN(62310, "未登录"),

	BAD_REQUEST(62400, "请求参数错误"),

	NOT_FOUND(62404, "请求地址错误"),

	METHOD_NOT_ALLOWED(62405, "请求方法错误"),

	UNSUPPORTED_MEDIA_TYPE(62415, "不支持的媒体类型"),

	INTERNAL_ERROR(62500, "服务异常"),
	
	NOT_EXTENDED(62510, "未知错误");


	private Integer code;
	private String msg;

	ResultType(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer code() {
		return code;
	}

	public String msg() {
		return msg;
	}

	public static ResultType getResultTypeByCode(int code) {
		for (ResultType obj : ResultType.values()) {
			if (obj.code() == code) {
				return obj;
			}
		}
		return ResultType.NOT_EXTENDED;
	}
}
