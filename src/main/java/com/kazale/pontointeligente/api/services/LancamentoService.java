package com.kazale.pontointeligente.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.kazale.pontointeligente.api.entities.Lancamento;

public interface LancamentoService {

	/**
	 * Retorna uma lista paginada de lancamentos de um determinado funcionario
	 * @param funcionarioId
	 * @param pageRequest
	 * @return Page<Lancamento>
	 */
	Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);
	//Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, QPageRequest pageRequest);
	
	/**
	 * Retorna um lancamento por ID
	 * @param Id
	 * @return Optional<Lancamento>
	 */
	Optional<Lancamento> buscarPorId(Long Id);
	
	/**
	 * Persiste um lancamento na base de dados
	 * @param lancamento
	 * @return Lancamento
	 */
	Lancamento persistir(Lancamento lancamento);
	
	/**
	 * Remove um lancamento na base de dados
	 * @param id
	 */
	void remover(Long id);
}
