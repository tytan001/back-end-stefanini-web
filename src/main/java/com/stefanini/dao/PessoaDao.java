package com.stefanini.dao;

import com.stefanini.dao.abstracao.GenericDao;
import com.stefanini.model.Pessoa;

import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * O Unico objetivo da Dao 
 * @author joaopedromilhome
 *
 */
public class PessoaDao extends GenericDao<Pessoa, Long> {

	public PessoaDao() {
		super(Pessoa.class);
	}

	/**
	 * Efetuando busca de Pessoa por email
	 * @param email
	 * @return
	 */
	public Optional<Pessoa> buscarPessoaPorEmail(String email){
		TypedQuery<Pessoa> q2 =
				entityManager.createQuery(" select p from Pessoa p where p.email=:email", Pessoa.class);
		q2.setParameter("email", email);
		return q2.getResultStream().findFirst();
	}
	
	public List<Pessoa> buscaOtimizada(){
//		StringBuilder sql = new StringBuilder();
//		
//		sql.append("SELECT p FROM Pessoa p ");
//		sql.append("LEFT JOIN FETCH p.enderecos enderecos ");
//		sql.append("LEFT JOIN FETCH p.perfils perfils ");
//		sql.append("ORDER BY p.nome");
		
//		TypedQuery<Pessoa> query = getEntityManager().createQuery(sql.toString(), Pessoa.class);
		TypedQuery<Pessoa> query = getEntityManager().createNamedQuery("Pessoa.findPerfilsAndEnderecosByNome", Pessoa.class);
		
		return query.getResultList();
	
	}
	
	public Pessoa salvarEntity(Pessoa pessoaDto) {
		Pessoa pessoa = new Pessoa(
				pessoaDto.getNome(),
				pessoaDto.getEmail(),
				pessoaDto.getDataNascimento(),
				pessoaDto.getSituacao(),
				pessoaDto.getCaminhoImagem(),
				pessoaDto.getPerfils());

		this.getEntityManager().persist(pessoa);

		if(pessoaDto.getEnderecos() != null) {
			pessoaDto.getEnderecos().forEach(x -> {
				x.setIdPessoa(pessoa.getId());
				try {
					this.getEntityManager().persist(x);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		return pessoa;
	}

}
