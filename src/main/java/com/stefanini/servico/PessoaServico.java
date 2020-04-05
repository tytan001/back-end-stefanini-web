package com.stefanini.servico;

import com.stefanini.dao.PessoaDao;
import com.stefanini.dto.PessoaDto;
import com.stefanini.exception.NegocioException;
import com.stefanini.model.Imagem;
import com.stefanini.model.Pessoa;

import javax.ejb.*;
import javax.inject.Inject;
import javax.validation.Valid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 
 * Classe de servico, as regras de negocio devem estar nessa classe
 * 
 * @author joaopedromilhome
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PessoaServico implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String pathFileBase = "C:\\Users\\dev\\Documents\\stefanini\\imagens\\";

	@Inject
	private PessoaDao dao;

	@Inject
	private PessoaPerfilServico pessoaPerfilServico;

	/**
	 * Salvar os dados de uma Pessoa
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public PessoaDto salvar(@Valid Pessoa pessoa) {
		return toPessoaDTO(dao.salvarEntity(pessoa));
	}

	/**
	 * Validando se existe pessoa com email
	 */
	public Boolean validarPessoa(@Valid Pessoa pessoa) {
		if (pessoa.getId() != null) {
			Optional<Pessoa> encontrar = dao.encontrar(pessoa.getId());
			if (encontrar.get().getEmail().equals(pessoa.getEmail())) {
				return Boolean.TRUE;
			}
		}
		Optional<Pessoa> pessoa1 = dao.buscarPessoaPorEmail(pessoa.getEmail());
		return pessoa1.isEmpty();
	}

	/**
	 * Atualizar o dados de uma pessoa
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public PessoaDto atualizar(@Valid Pessoa entity) {
		return toPessoaDTO(dao.atualizar(entity));
	}

	/**
	 * Remover uma pessoa pelo id
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void remover(@Valid Long id) throws NegocioException {
		if (pessoaPerfilServico.buscarPessoaPerfil(id, null).count() == 0) {
			dao.remover(id);
			return;
		}
		throw new NegocioException("Não foi possivel remover a pessoa");
	}

	/**
	 * Buscar uma lista de Pessoa
	 */
	public Optional<List<Pessoa>> getList() {
		return dao.getList();
	}

	/**
	 * Buscar uma lista de Pessoa de forma mais otimizada
	 */
	public Optional<List<PessoaDto>> getListOtimizada() {
		List<Pessoa> pessoas = dao.buscaOtimizada();
		List<PessoaDto> pessoasDTO = new ArrayList<PessoaDto>();
		for (Pessoa pessoa : pessoas) {
			pessoasDTO.add(toPessoaDTO(pessoa));
		}
		return Optional.of(pessoasDTO);
	}

	/**
	 * Buscar uma Pessoa pelo ID
	 */
//	@Override
	public Optional<PessoaDto> encontrar(Long id) {
		Optional<Pessoa> pessoa = dao.encontrar(id);
		return Optional.of(toPessoaDTO(pessoa.get()));
	}

	/**
	 * Metodos de transforma DTO em entidade
	 */
	public Pessoa toPessoa(PessoaDto dto) {
		return new Pessoa(dto.getId(), dto.getNome(), dto.getEmail(), dto.getDataNascimento(), dto.getSituacao(),
				dto.getEnderecos(), dto.getPerfils());

	}

	/**
	 * Metodos de transforma entidade em DTO
	 */
	public PessoaDto toPessoaDTO(Pessoa pessoa) {
		PessoaDto pessoaDto = new PessoaDto(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(),
				pessoa.getDataNascimento(), pessoa.getSituacao(), pessoa.getEnderecos(), pessoa.getPerfils());
		if (pessoa.getCaminhoImagem() != null) {
			String nomeImagem = getNameImage(pessoa.getCaminhoImagem());
			pessoaDto.setImagem(
					new Imagem(nomeImagem, getTipoImage(nomeImagem), getImageBase64(pessoa.getCaminhoImagem())));
		} else
			pessoaDto.setImagem(new Imagem());
		return pessoaDto;

	}

	/**
	 * Metodo de salvar a imagem e retorna o local onde foi salva
	 */
	public String saveImage(String name, String base64) {
		String pathFile = pathFileBase + name;
		try {
			FileOutputStream imageOutFile = new FileOutputStream(pathFile);
			byte[] imageByteArray = Base64.getDecoder().decode(base64);
			imageOutFile.write(imageByteArray);

		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
			return null;
		} catch (IOException ioe) {
			System.out.println("Exception while writing the Image " + ioe);
			return null;
		}
		return pathFile;
	}

	/**
	 * Metodo de pegar uma imagem com base no path onde foi salva
	 */
	public String getImageBase64(String imagePath) {
		String base64Image = "";
		File file = new File(imagePath);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			base64Image = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
		return base64Image;
	}

	/**
	 * Metodo de pegar o nome da imagem salvo no path onde foi salva
	 */
	private String getNameImage(String imagePath) {
		return imagePath != null ? imagePath.replace(pathFileBase, "") : "";
	}

	/**
	 * Metodo de pegar o tipo da imagem com base no nome dela
	 */
	private String getTipoImage(String nameImage) {
		String retorno = "";
		String[] textoSeparado = nameImage.split("\\.");

		if (textoSeparado != null && textoSeparado.length != 0)
			switch (textoSeparado[textoSeparado.length - 1]) {
			case "jpg":
				retorno = "image/jpeg";
				break;
			case "png":
				retorno = "image/png";
				break;
			default:
				retorno = "image/" + textoSeparado[textoSeparado.length - 1];
			}

		return retorno;
	}

}
