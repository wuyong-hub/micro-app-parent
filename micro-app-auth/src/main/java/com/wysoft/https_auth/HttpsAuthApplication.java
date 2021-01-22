package com.wysoft.https_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages= {"com.wysoft.https_base","com.wysoft.https_auth"})
@EnableJpaRepositories
public class HttpsAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpsAuthApplication.class,args);
	}
}

