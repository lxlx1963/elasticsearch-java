package com.du.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
	/**
	 * 终端类型
	 */
	private Integer deviceType;
	/**
	 * 限定人数
	 */
	private Integer limitPeopleNum;

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getLimitPeopleNum() {
		return limitPeopleNum;
	}

	public void setLimitPeopleNum(Integer limitPeopleNum) {
		this.limitPeopleNum = limitPeopleNum;
	}
}
