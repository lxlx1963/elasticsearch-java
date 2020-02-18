package com.du.elasticsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfig {

	/**
	 * 自定义配置处理类
	 */
	@Bean
	public CustomProperties customProperties() {
		return new CustomProperties();
	}

}
