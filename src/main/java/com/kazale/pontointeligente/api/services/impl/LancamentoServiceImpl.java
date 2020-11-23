package com.kazale.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
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
		public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, QPageRequest pageRequest) {
			log.info("Buscando lancamentos para o funcionario ID {}", funcionarioId);
			return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
		}

		@Override
		public Optional<Lancamento> buscarPorId(Long Id) {
			log.info("Buscando lancamentos pelo ID {}", Id);
			return Optional.ofNullable(this.lancamentoRepository.findOne(Id));
		}

		@Override
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
