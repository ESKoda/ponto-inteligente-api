package com.kazale.pontointeligente.api.controllers;

import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;
import java.text.ParseException;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kazale.pontointeligente.api.dtos.LancamentoDto;
import com.kazale.pontointeligente.api.entities.Funcionario;
import com.kazale.pontointeligente.api.entities.Lancamento;
import com.kazale.pontointeligente.api.enums.TipoEnum;
import com.kazale.pontointeligente.api.response.Response;
import com.kazale.pontointeligente.api.services.FuncionarioService;
import com.kazale.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*") //@CrossOrigin possibilitar acesso de qualquer localizacao
public class LancamentoController {

	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//definindo um formato de data, nesse caso o formato sera do padrao mysql
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Value("${paginacao.qtd_por_pagina}") //esta configurado no application.properties a qtd=25 (cada pagina tera 25 lancamentos)
	private int qtdPorPagina;
	
	public LancamentoController() {
		
	}
	
	//@PathVariable obter o parametro id do funcionario para passar na query
	//@RequestParam eh a ? na query, eh um parametro que pode ou nao receber 
	//fazendo Get pelo Postman (http://localhost:8080/api/lancamentos/funcionario/2?dir=ASC)
	//http://localhost:8080/api/lancamentos/funcionario/2?dir=ASC&pag=1
	/**
	 * Retorna a listagem de lancamentos de um funcionario buscando pelo id
	 * @param funcionarioId
	 * @return ResponseEntity<Response<Page<LancamentoDto>>>
	 */
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> listaPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag, //qual pagina vc quer obter os dados
			@RequestParam(value = "ord", defaultValue = "id") String ord, //sera ordenado pelo campo id
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) { //em ordem decrscente
		
		log.info("Buscando lancamentos por ID do funcionario: {}, pagina: {}", funcionarioId,pag);
		Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
		
		PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		Page<LancamentoDto> lancamentoDto = lancamentos.map(lancamento -> this.converterLancamentoDto(lancamento));
		//map() java 8, converter o Lancamento em LancamentoDto
		
		response.setData(lancamentoDto);
		return ResponseEntity.ok(response); //OK resposta: 200
	}

	/**
	 * Converte uma entidade lancamento para seu respectivo DTO
	 * @param lancamento
	 * @return LancamentoDto
	 */
	private LancamentoDto converterLancamentoDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		
		return lancamentoDto;
	}
	
	/**
	 * Retorna um lancamento por ID
	 * @param id
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
		log.info("Buscando lancamento por ID: {}", id);
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()) {
			log.info("Lancamento nao encontrado para o ID: {}", id);
			response.getErrors().add("Lancamento nao encontrado para o id: {}" + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(this.converterLancamentoDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Adiciona um novo lancamento
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto,
			BindingResult result) throws ParseException{
		
		log.info("Adicionando lancamento: {}", lancamentoDto.toString());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		validarFuncionario(lancamentoDto,result);
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto,result);
		
		if(result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento); //estando tudo ok, gravamos no banco
		response.setData(this.converterLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
	}

	/**
	 * Converte um LancamentoDto para uma entidade Lancamento
	 * @param lancamentoDto
	 * @param result
	 * @return lancamento
	 * @throws ParseException
	 */
	private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException  {
		Lancamento lancamento = new Lancamento();
		
		if(lancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
			if(lanc.isPresent()) {
				lancamento = lanc.get();
			}else {
				result.addError(new ObjectError("lancamento", "Lancamento nao encontrado"));
			}
		}else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
		}
		
		lancamento.setDescricao(lancamentoDto.getDescricao());
		lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));
		
		//EnumUtils verifica se o que foi passado como parametro existe realmente na nossa classe Enum TipoEnum (Inicio trabalaho, termino trabalho...)  
		if(EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
		}else {
			result.addError(new ObjectError("tipo", "Tipo invalido"));
		}
		return lancamento;
	}

	/**
	 * Valida um funcionario, verificando se ele eh existente e valido no sistema
	 * @param lancamentoDto
	 * @param result
	 */
	private void validarFuncionario(LancamentoDto lancamentoDto, BindingResult result) {
		if(lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionario nao informado"));
			return;
		}
		
		log.info("Validando funcionario id {}: ", lancamentoDto.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
		if(!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario nao encontrado, ID inexistente"));
		}
	}
	
	/**
	 * Atualiza os dados de um lancamento
	 * @param id
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException{
				
		log.info("Atualizando lancamento: {}", lancamentoDto.toString());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		validarFuncionario(lancamentoDto, result);
		lancamentoDto.setId(Optional.of(id));
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validadno lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentoDto(lancamento));
		return ResponseEntity.ok(response);		
	}
	
	/**
	 * Deleta um cliente informando um ID
	 * @param id
	 * @return ResponseEntity<Response<String>>
	 */
	//fazendo Delete pelo Postman (http://localhost:8080/api/lancamentos/2)
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover (@PathVariable("id") Long id){
		log.info("Removendo lancamento: {}", id);
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()) {
			log.info("Erro ao remover devido ao lancamento ID: {} ser invalido", id);
			response.getErrors().add("Erro ao remover lancamento. Registro nao encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}
}
