package com.kazale.pontointeligente.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.kazale.pontointeligente.api.entities.Empresa;
import com.kazale.pontointeligente.api.repositories.EmpresaRepository;

@SpringBootTest
@ActiveProfiles("test")
public class EmpresaServiceTest {

	@MockBean
	private EmpresaRepository empresaRepository;
	
	@Autowired //obter instancia verdade do objeto
	private EmpresaService empresaService;
	
	private static final String CNPJ = "51463645000100";
	
	//BDDMockito objetos falsos para testes, simular chamadas
	@BeforeEach
	public void setUp() throws Exception{
		BDDMockito.given(this.empresaRepository.findByCnpj(Mockito.anyString())).willReturn(new Empresa());
		BDDMockito.given(this.empresaRepository.save(Mockito.any(Empresa.class))).willReturn(new Empresa());
	//BDDMockito.given: dado o metodo, ou para cada execucao do metodo x
	//willReturn: retorne y
	}
	
	@Test
	public void testBuscarEmpresaPorCnpj() {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(CNPJ); //bucarPorCnpj(CNPJ) vai pegar do Mockito (willReturn(new Empresa()))
		
		assertTrue(empresa.isPresent()); //isPresent() certificando que existe uma empresa dentro de Optional<Empresa>
		
	}
	
	@Test
	public void testPersistirEmpresa() {
		Empresa empresa = this.empresaService.persistir(new Empresa()); //Mockito vai passar uma nova empresa no new Empresa()
		
		assertNotNull(empresa);
	}
}
