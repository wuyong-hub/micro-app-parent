package com.wysoft.https_note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages= {"com.wysoft.https_base","com.wysoft.https_note"})
@EnableJpaRepositories
public class HttpsNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpsNoteApplication.class,args);
	}

}
