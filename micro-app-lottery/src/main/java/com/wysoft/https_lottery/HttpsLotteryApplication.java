package com.wysoft.https_lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages= {"com.wysoft.https_base","com.wysoft.https_lottery"})
@EnableJpaRepositories
public class HttpsLotteryApplication {
	public static void main(String []args) {
		SpringApplication.run(HttpsLotteryApplication.class, args);
	}
}
