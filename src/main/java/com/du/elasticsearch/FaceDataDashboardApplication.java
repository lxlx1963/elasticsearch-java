package com.du.elasticsearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = "com.xinchao.data.dao")
public class FaceDataDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaceDataDashboardApplication.class, args);
	}

}
