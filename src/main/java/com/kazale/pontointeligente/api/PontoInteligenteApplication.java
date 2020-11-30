package com.kazale.pontointeligente.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //habilitando o ehcache
public class PontoInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PontoInteligenteApplication.class, args);
	}

}
