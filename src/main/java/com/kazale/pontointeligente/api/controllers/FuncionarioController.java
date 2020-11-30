package com.kazale.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kazale.pontointeligente.api.dtos.FuncionarioDto;
import com.kazale.pontointeligente.api.entities.Funcionario;
import com.kazale.pontointeligente.api.response.Response;
import com.kazale.pontointeligente.api.services.FuncionarioService;
import com.kazale.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {
	
	private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public FuncionarioController() {
		
	}
	
	/**
	 * Atualiza os dados de um funcionario
	 * @param id
	 * @param funcionarioDto
	 * @param result
	 * @return ResponseEntity<Response<FuncionarioDto>>
	 * @throws NoSuchAlgorithmException
	 */
	
	//@PathVariable buscando o id para saber o que atualizar
	//@Valid valida dados de entrada
	//BindingResult pegar o resultado da validacao
	@PutMapping(value = "/{id}") //@PutMapping faz update no banco
	public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{
		log.info("Atualizando funcionario: {}", funcionarioDto.toString());
		Response<FuncionarioDto> response = new Response<FuncionarioDto>();
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		if(!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario nao encontrado"));
		}
		
		//funcionario.get() trabalha com o Optional para obter instancia do Funcionario
		this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);
		
		//se existir algum erro na validacao
		if(result.hasErrors()) {
			log.error("Erro validando funcionario: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		//caso nao tenha erros entao iremos persistir
		//funcionario.get(): Funcionario eh um Optional, entao fazemos get() para obter instancia do Funcionario
		this.funcionarioService.persistir(funcionario.get());
		//converter de entidade Funcionario para um Dto FuncionarioDto e setar como dados do response
		response.setData(this.converterFuncionarioDto(funcionario.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna um DTO com os dados de um funcionario no json
	 * @param funcionario
	 * @return FuncionarioDto
	 */
	private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
		FuncionarioDto funcionarioDto = new FuncionarioDto();
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setEmail(funcionario.getEmail());
		funcionarioDto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> funcionarioDto.setQtsHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt().ifPresent(
				valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));
		return funcionarioDto;
		//nao convertemos a senha pq esse atributo nao podemos retornar para o cliente, por ser confidencial
	}

	/**
	 * Atualiza os dados do funcionario com base nos dados encontrados no DTO
	 * @param funcionario
	 * @param funcionarioDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */
	private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto,
			BindingResult result) throws NoSuchAlgorithmException{
		
		funcionario.setNome(funcionarioDto.getNome());
		
		//se o email que esta sendo passado como parametro for diferente do que existe no banco
		if(!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("email", "Email ja existente")));//buscando no banco se o email ja existe
			funcionario.setEmail(funcionarioDto.getEmail()); //senao existir no banco entao atualizamos
		}
		
		funcionario.setQtdHorasAlmoco(null);
		funcionarioDto.getQtsHorasAlmoco()
			.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));//se estiver sendo passado o QtdHorasAlmoco como parametro entao atualizamos no banco com o valor do funcionarioDto
		
		funcionario.setQtdHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		
		funcionario.setValorHora(null);
		funcionarioDto.getValorHora()
			.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		//se estiver sendo passado a senha como parametro entao atualizamos no banco com o valor do funcionarioDto
		if(funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
		}
	}
}
