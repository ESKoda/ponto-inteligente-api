package com.kazale.pontointeligente.api.response;

import java.util.ArrayList;
import java.util.List;

//<T>: se chama Generics, estamos especificando que o Response soh sabe guardar referencias do tipo que passarmos entre <>, e vai armazenar esse tipo que for passado
public class Response<T> {

	private T data;
	private List<String> errors;
	
	public Response() {
		
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data=data;
	}
	
	public List<String> getErrors(){
		if(this.errors == null) {
			this.errors = new ArrayList<String>();
		}
		return errors;
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}

/*
Response: 
	Encapsulando o retorno de uma chamada a API Restful
Problema: 
	Gostaria que todos os retornos de minha API Restful fossem padronizadas e tivessem a mesma estrutura.
Solução: 
	Para padronizar o retorno das requisições de uma API Restful, o indicado é a criação de
	uma classe responsável por encapsular os dados de retorno de um modo estruturado.
	Por isso devemos criar uma classe ‘Response’, que conterá uma estrutura mínima para
	manipular os casos de sucesso ou erro em uma requisição, mantendo assim toda a
	estrutura da API padronizada.
Como fazer:
	Primeiramente vamos criar uma classe para encapsular todas as requisições HTTP de nossa API.

Na classe acima foram definidos basicamente dois atributos, ‘data’ para conter os dados em
caso de sucesso, e ‘errors’ para armazenar mensagens de erros do sistemas.
Nesse caso, todas as requisições seguirão um mesmo padrão
*/