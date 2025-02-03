package br.com.gestao.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.gestao.commons.ResponseWrapper;
import br.com.gestao.dto.TelefoneDTO;
import br.com.gestao.entities.Telefone;
import br.com.gestao.repositories.TelefoneRepository;

@Service
public class TelefoneService {

	private final TelefoneRepository telefoneRepository;
	private final ModelMapper modelMapper;

	public TelefoneService(TelefoneRepository telefoneRepository, ModelMapper modelMapper) {
		this.telefoneRepository = telefoneRepository;
		this.modelMapper = modelMapper;
	}

	// LISTAR POR ID
	public ResponseEntity<ResponseWrapper<TelefoneDTO>> findById(Long id) {
		Telefone telefone = telefoneRepository.findById(id).orElse(null);
		if (telefone != null) {
			return new ResponseEntity<>(new ResponseWrapper<>(this.modelMapper.map(telefone, TelefoneDTO.class), null),HttpStatus.OK);
		} else {
			ResponseWrapper<TelefoneDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Cadastro ID: " + id + " Não encontrado!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS
	public ResponseEntity<ResponseWrapper<List<TelefoneDTO>>> findAll() {
		List<TelefoneDTO> enderecos = telefoneRepository.findAll().stream().map(valorCC -> modelMapper.map(valorCC, TelefoneDTO.class)).collect(Collectors.toList());
		if(enderecos != null && !enderecos.isEmpty())
		{
			return new ResponseEntity<>(new ResponseWrapper<>(enderecos, null), HttpStatus.OK);
		}else {
			ResponseWrapper<List<TelefoneDTO>> responseWrapper = new ResponseWrapper<>();
	        responseWrapper.setMessage("Base de dados não tem nenhum usuário cadastrado");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS E MOSTRAR O RESULTADO UTILIZANDO PAGINAÇÃO
	public ResponseEntity<ResponseWrapper<Page<TelefoneDTO>>> findAll(Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Telefone> page = this.telefoneRepository.listAllByPages(pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<TelefoneDTO> dtoPage = page.map(item -> this.modelMapper.map(item, TelefoneDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		}else {
			ResponseWrapper<Page<TelefoneDTO>> responseWrapper = new ResponseWrapper<>();
	        responseWrapper.setMessage("Não foram encontrados endereços na base de dados");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}
}
