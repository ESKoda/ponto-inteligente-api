package com.kazale.pontointeligente.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kazale.pontointeligente.api.dtos.LancamentoDto;
import com.kazale.pontointeligente.api.entities.Funcionario;
import com.kazale.pontointeligente.api.entities.Lancamento;
import com.kazale.pontointeligente.api.enums.TipoEnum;
import com.kazale.pontointeligente.api.services.FuncionarioService;
import com.kazale.pontointeligente.api.services.LancamentoService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LancamentoControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	@MockBean
	private FuncionarioService funcionarioService;
	
	private static final String URL_BASE="/api/lancamentos/";
	private static final Long ID_FUNCIONARIO=1L;
	private static final Long ID_LANCAMENTO=1L;
	private static final String TIPO = TipoEnum.INICIO_TRABALHO.name();
	private static final Date DATA = new Date();
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	@WithMockUser //spring security
	public void testCadastrarLancamento() throws Exception{
		Lancamento lancamento = obterDadosLancamento();
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Funcionario()));
		BDDMockito.given(this.lancamentoService.persistir(Mockito.any(Lancamento.class))).willReturn(lancamento);
		
		mvc.perform(MockMvcRequestBuilders.post(URL_BASE) //post: faz um post request
			.content(this.obterJsonRequisicaoPost()) //passando um objeto json no corpo da requisicao
			.contentType(MediaType.APPLICATION_JSON) //eh do tipo json
			.accept(MediaType.APPLICATION_JSON)) //aceita json
			.andExpect(status().isOk()) //se esta status ok
			.andExpect(jsonPath("$.data.id").value(ID_LANCAMENTO)) //validamos se foi o resultado experado dos dados do json com o dados que colocamos nas variaveis
			.andExpect(jsonPath("$.data.tipo").value(TIPO))
			.andExpect(jsonPath("$.data.data").value(this.dateFormat.format(DATA)))
			.andExpect(jsonPath("$.data.funcionarioId").value(ID_FUNCIONARIO))
			.andExpect(jsonPath("$.errors").isEmpty()); //senao existe erros
		
	}

	@Test
	@WithMockUser //spring security
	public void testCadastrarLancamentoFuncionarioIdInvalido() throws Exception{
		//simulando um retorno vazio, como senao encontrasse um funcionario
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.empty());
		
		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
				.content(this.obterJsonRequisicaoPost())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()) //experado um bad request
				.andExpect(jsonPath("$.errors").value("Funcionario nao encontrado, ID inexistente")) //experado um erro  Funcionario nao encontrado
				.andExpect(jsonPath("$.data").isEmpty()); //experado que os dados estejam vazio
	}

	@Test
	@WithMockUser(username = "admin@ekoda.com", roles = {"ADMIN"}) //spring security
	public void testRemoverLancamento() throws Exception{
		//simulando que pegou um lancamento
		BDDMockito.given(this.lancamentoService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Lancamento()));
		
		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO) //deletando o lancamento
				.accept(MediaType.APPLICATION_JSON)) //informando que eh do tipo json
				.andExpect(status().isOk()); //experado que de OK, que foi deletado
	}
	
	private String obterJsonRequisicaoPost() throws JsonProcessingException{
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(null);
		lancamentoDto.setData(this.dateFormat.format(DATA));
		lancamentoDto.setTipo(TIPO);
		lancamentoDto.setFuncionarioId(ID_FUNCIONARIO);
		ObjectMapper mapper = new ObjectMapper();//instanciando um objeto json
		return mapper.writeValueAsString(lancamentoDto); //convertendo o lancamentoDto em um objeto json
	}

	private Lancamento obterDadosLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(ID_LANCAMENTO);
		lancamento.setData(DATA);
		lancamento.setTipo(TipoEnum.valueOf(TIPO));
		lancamento.setFuncionario(new Funcionario());
		lancamento.getFuncionario().setId(ID_FUNCIONARIO);
		return lancamento;
	}
	
	@Test
	@WithMockUser
	public void testRemoverLancamentoAcessoNegado() throws Exception {
		BDDMockito.given(this.lancamentoService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Lancamento()));

		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
}
