package com.gearsy.gearsy;


import com.gearsy.gearsy.security.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GearsYApplication {

	public static void main(String[] args) {
		SpringApplication.run(GearsYApplication.class, args);
	}

	// In ra JWT khi app start
	@Bean
	CommandLineRunner printJwt(JwtUtils jwtConfig) {
		return args -> {

		};
	}
}
