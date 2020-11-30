package com.kazale.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.kazale.pontointeligente.api.entities.Empresa;
import com.kazale.pontointeligente.api.services.EmpresaService;

@SpringBootTest
@AutoConfigureMockMvc //contexto web
@ActiveProfiles("test")
public class EmpresaControllerTest {

	@Autowired //obter instancia da classe MockMvc
	private MockMvc mvc;
	
	@MockBean
	private EmpresaService empresaService;
	
	private static final String BUSCAR_EMPRESA_CNPJ_URL = "/api/empresas/cnpj/";
	private static final Long ID = Long.valueOf(1);
	private static final String CNPJ = "51463645000100";
	private static final String RAZAO_SOCIAL="Empresa XYZ";
	
	@Test
	@WithMockUser //spring security
	public void testBuscarEmpresaCnpjInvalido() throws Exception{
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString()))
			.willReturn(Optional.empty());//simulando que foi buscar um cnpj no banco e retornou vazio
		
		//perform: execute
		//get() fazendo get pelo cnpj
		//accept informando que estamos trabalhando com json
		mvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest()) //andExpect experado badRequest
			.andExpect(jsonPath("$.errors").value("Empresa nao encontrada para o CNPJ " + CNPJ)); //experado erro no json com valor "Empresa nao encontrada para o CNPJ" 
		
	}
	
	@Test
	@WithMockUser //spring security
	public void testBuscarEmpresaCnpjValido() throws Exception{
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString()))
			.willReturn(Optional.of(this.obterDadosEmpresa()));
		
		mvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(ID))
			.andExpect(jsonPath("$.data.razaoSocial", equalTo(RAZAO_SOCIAL)))
			.andExpect(jsonPath("$.data.cnpj", equalTo(CNPJ)))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setId(ID);
		empresa.setRazaoSocial(RAZAO_SOCIAL);
		empresa.setCnpj(CNPJ);
		return empresa;
	}
}