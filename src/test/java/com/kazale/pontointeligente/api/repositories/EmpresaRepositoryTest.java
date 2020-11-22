package com.kazale.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.kazale.pontointeligente.api.entities.Empresa;

@RunWith(SpringRunner.class) //annotation para fazer testes
@SpringBootTest				//annotation para fazer testes
@ActiveProfiles("test")
public class EmpresaRepositoryTest {

	@Autowired //para ter acesso a EmpresaRepository
	private EmpresaRepository empresaRepository;
	
	private static final String CNPJ="51463645000100";
	
	@Before //executa antes da execucao do teste
	public void setUp() throws Exception{
		Empresa empresa=new Empresa();
		empresa.setRazaoSocial("Empresa de exemplo");
		empresa.setCnpj(CNPJ);
		this.empresaRepository.save(empresa);
				
	}
	
	@After //executa depois da execucao do teste
	public final void tearDown() {
		this.empresaRepository.deleteAll();
	}
	
	@Test
	public void testBuscarPorCnpj() {
		Empresa empresa = this.empresaRepository.findByCnpj(CNPJ);
		
		assertEquals(CNPJ, empresa.getCnpj());
	}
}
