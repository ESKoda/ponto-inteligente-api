package com.kazale.pontointeligente.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PasswordUtils.class);

	public PasswordUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	* Gera um hash utilizando o BCrypt.
	*
	* @param senha
	* @return String
	*/
	public static String gerarBCrypt(String senha) {
		if (senha == null) {
			return senha ;
		}
		
		log.info("Gerando hash com o BCrypt.");
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
		return bCryptEncoder.encode(senha);
	}

//	/**
//	* Verifica se a senha é válida.
//	*
//	* @param senha
//	* @param senhaEncoded
//	* @return boolean
//	*/
//	public static boolean senhaValida(String senha, String senhaEncoded) {
//		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
//		return bCryptEncoder.matches(senha,senhaEncoded);
//	}
		

}
