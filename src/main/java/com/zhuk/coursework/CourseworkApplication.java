package com.zhuk.coursework;

import com.zhuk.coursework.config.AppConfig;
import com.zhuk.coursework.config.LiquibaseConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OpenAPIDefinition(info = @Info(title = "Medication API", version = "1.0"))
@EnableConfigurationProperties({AppConfig.class, LiquibaseConfig.class})
@SpringBootApplication
public class CourseworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(CourseworkApplication.class, args);
	}
}
