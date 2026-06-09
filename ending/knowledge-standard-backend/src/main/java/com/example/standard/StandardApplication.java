package com.example.standard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;   // 新增导入

@SpringBootApplication
@MapperScan("com.example.standard.mapper")
public class StandardApplication {
	public static void main(String[] args) {
		SpringApplication.run(StandardApplication.class, args);
	}
}