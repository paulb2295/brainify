package com.bpx.brainify;

import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import com.bpx.brainify.services.interfaces.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.bpx.brainify.enums.Role.ADMIN;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BrainifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrainifyApplication.class, args);
	}

}
