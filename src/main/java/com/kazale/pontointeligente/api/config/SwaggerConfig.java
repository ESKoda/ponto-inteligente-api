package com.kazale.pontointeligente.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.kazale.pontointeligente.api.security.utils.JwtTokenUtil;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration //arquivo de config
@Profile("dev") //soh funciona se o profile estiver setado como dev
@EnableSwagger2
public class SwaggerConfig {
	
	//Classe para INTERFACE WEB
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select() //selecione todas as classe dentro de controllers
				.apis(RequestHandlerSelectors.basePackage("com.kazale.pontointeligente.api.controllers"))
				.paths(PathSelectors.any()).build() //para buscar qualquer tipo de informacao
				.apiInfo(apiInfo()); //customizando a interface com titulo
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Ponto Inteligente API")
				.description("Documentacao do API de acesso aos endpoints do Ponto Inteligente").version("1.0")
				.build();
	}
	
	//logando com o user "admin@bruna.com" de forma automatica
	@Bean
	public SecurityConfiguration security() {
		String token;
		try {
			///o email"bruna@teste.com" esta no insert do flyway no caminho: PontoInteligente/src/main/resources/db/migration/mysql/V2__admin_padrao.sql
			UserDetails userDetails = this.userDetailsService.loadUserByUsername("admin@kazale.com");
			token = this.jwtTokenUtil.obterToken(userDetails); //obtendo o token para logar
			
		}catch(Exception e) {
			token = "";
		}
		return new SecurityConfiguration(null, null, null, null, "Bearer " + token, ApiKeyVehicle.HEADER,
				"Authorization", ",");
	}
}
