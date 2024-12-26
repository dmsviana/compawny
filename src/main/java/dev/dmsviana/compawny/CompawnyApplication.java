package dev.dmsviana.compawny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CompawnyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompawnyApplication.class, args);
	}

}
