package com.kazale.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kazale.pontointeligente.api.entities.Lancamento;
import com.kazale.pontointeligente.api.repositories.LancamentoRepository;
import com.kazale.pontointeligente.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

		private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);

		@Autowired
		private LancamentoRepository lancamentoRepository;
		
		@Override
		//public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, QPageRequest pageRequest) {
		public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
			log.info("Buscando lancamentos para o funcionario ID {}", funcionarioId);
			return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
		}

		//@Override		
		//utilizando o ehcache, lancamentoPorId esta configurado no arquivo (/PontoInteligente/src/main/resources/ehcache.xml)
		@Cacheable("lancamentoPorId") //ira cachear o resultado da buscarPorId()
		public Optional<Lancamento> buscarPorId(Long id) {
			log.info("Buscando lancamentos pelo ID {}", id);
			return this.lancamentoRepository.findById(id);
		}

		//@Override
		//@CachePut mantem o cache (lancamentoPorId) atualizado, caso tenha alguma alteracao no dado o cache tbm sera atualizado
		@CachePut("lancamentoPorId")
		public Lancamento persistir(Lancamento lancamento) {
			log.info("Persistindo o lancamento: {}", lancamento);
			return this.lancamentoRepository.save(lancamento);
		}

		@Override
		public void remover(Long id) {
			log.info("Removendo o lancamento ID: {}", id);
			this.lancamentoRepository.deleteById(id);
		}
		
		
}
