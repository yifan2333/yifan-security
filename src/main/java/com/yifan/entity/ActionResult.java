package com.yifan.entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 接口返回值封装
 * 
 * @param <T>
 * @author caowuchao
 * @since 2018年11月7日
 * @version 1.0
 */
public class ActionResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 状态码 */
	private int code;
	/** 消息 */
	private String message;
	/** 数据 */
	private T data;

	public ActionResult(Builder<T> builder) {
		this.code = builder.code;
		this.message = builder.message;
		this.data = builder.data;
	}

	public ActionResult() {
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean successful() {
		return ResultType.OK.code().equals(code);
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

	public static class Builder<T> {
		/** 状态码 */
		private int code;
		/** 消息 */
		private String message;
		/** 数据 */
		private T data;
		
		public Builder() {}

		public Builder<T> code(int code) {
			this.code = code;
			return this;
		}

		public Builder<T> message(String message) {
			this.message = message;
			return this;
		}

		public Builder<T> data(T data) {
			this.data = data;
			return this;
		}

		public Builder<T> resultType(ResultType resultType) {
			this.code = resultType.code();
			this.message = resultType.msg();
			return this;
		}

		public ActionResult<T> build() {
			initDefaultValue(this);
			return new ActionResult<>(this);
		}

		private void initDefaultValue(Builder<T> builder) {
			if (builder.code == 0) {
				builder.code = ResultType.OK.code();
			}
			if (builder.message == null) {
				builder.message = ResultType.OK.msg();
			}
		}
	}

}
