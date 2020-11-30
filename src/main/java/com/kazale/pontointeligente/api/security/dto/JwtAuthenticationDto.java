package com.kazale.pontointeligente.api.security.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

//Implementando a ação de login na aplicação, que dependerá de três arquivos:
//1)DTO para o usuário(JwtAuthenticationDto)
//2)DTO para retornar o token (TokenDto)
//3)controller de autenticação (AuthenticationController)
//Com esses três arquivos implementados, agora já é possível obter um token de autenticação, 
//para isso, adicione ao menos um usuário de teste ao banco de dados.
//Existe mais um último recurso que é a autorização por perfil, conforme definido no enum
//anteriormente, assim é possível validar cada ação de um controle.
//Para realizar tal validação, usamos a anotação ‘@PreAuthorize’, e no exemplo a seguir é
//validado se o usuário possui o perfil de administrador para acessar a ação do controller 
//na classe LancamentoController, no metodo remover (@PreAuthorize("hasAnyRole('ADMIN')")) 
//Para nao quebrar os tests unitarios na classe LancamentoControllerTest, adicionamos 
//no metodo testRemoverLancamento() a anotacao:
//@WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})

public class JwtAuthenticationDto {
	
	private String email;
	private String senha;

	public JwtAuthenticationDto() {
	}

	@NotEmpty(message = "Email não pode ser vazio.")
	@Email(message = "Email inválido.")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty(message = "Senha não pode ser vazia.")
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationRequestDto [email=" + email + ", senha=" + senha + "]";
	}

}
