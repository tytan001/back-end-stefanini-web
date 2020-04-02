package com.stefanini.servico;

import com.stefanini.dao.EnderecoDao;
import com.stefanini.model.Endereco;

import javax.ejb.*;
import javax.inject.Inject;
import javax.validation.Valid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

/**
 * 
 * Classe de servico, as regras de negocio devem estar nessa classe
 * @author joaopedromilhome
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EnderecoServico implements Serializable {
	
	@Inject
	private EnderecoDao dao;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Endereco salvar(@Valid Endereco entity) {
		return dao.salvar(entity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Endereco atualizar(@Valid Endereco entity) {
		return dao.atualizar(entity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remover(Long id) {
	dao.remover(id);
	}

	public Optional<List<Endereco>> getList() {
		return dao.getList();
	}

	public Optional<Endereco> encontrar(Long id) {
		return dao.encontrar(id);
	}
	
	public String buscarCep(String cep) {
		String json;
		
		try {
			URL url = new URL("https://viacep.com.br/ws/" + cep + "/json");
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			StringBuilder jsonSb = new StringBuilder();
			br.lines().forEach(l-> jsonSb.append(l.trim()));
			json = jsonSb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return json;
	}
}
