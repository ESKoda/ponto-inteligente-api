package com.kazale.pontointeligente.api.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

//classe que será responsável por tratar um erro de autenticação
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Acesso negado. Você deve estar autenticado no sistema para acessar a URL solicitada.");
	}

}
/*
Autenticação e autorização com tokens JWT (Json Web Token)
Problema:
	Gostaria de controlar o acesso a minha aplicação, tanto no login, quanto nas ações a serem
	acessadas por determinados funcionarios dentro da aplicação.
	Gostaria também que a autenticação fosse stateless, ou seja, que não dependesse de sessão, 
	e que utilizasse tokens JWT (JSON Web Token) para o armazenamento das credenciais do funcionario.
Solução:
	APIs Restful eficientes não devem manter estado, e devem permitir que sejam escaláveis
	horizontalmente, para que assim sejam de alta performance.
	Por ela não manter sessão em nenhum local, os dados de acesso devem estar
	armazenados em algum lugar que possa ser compartilhado entre requisições.
	Para isso que existe o JWT, que é um formato de token seguro e assinado digitalmente,
	garantindo a integridade dos dados trafegados. Dessa forma manteremos as informações de autenticação
	no token, para que assim a aplicação seja capaz de validar o acesso a uma requisição.
	As principais informações a serem armazenadas no token são dados do usuário, perfil e
	data de expiração do token.
Como fazer:
	A parte de implementação da autenticação e autorização utilizando tokens JWT não vem
	implementada por padrão no Spring, então ela demanda uma série de códigos e
	implementação de interfaces do Spring Security.


*/