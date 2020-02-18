package com.du.elasticsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局CROS配置
 *
 * @author dxy
 * @date 2019/3/14 17:44
 */
@Configuration
public class CORSConfiguration {
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//设置访问的IP和端口，此代表所有都可以访问
				registry.addMapping("/**");
			}
		};
	}
}
