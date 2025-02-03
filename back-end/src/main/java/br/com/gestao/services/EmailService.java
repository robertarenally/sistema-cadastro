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
import br.com.gestao.dto.EmailDTO;
import br.com.gestao.entities.Email;
import br.com.gestao.repositories.EmailRepository;

@Service
public class EmailService {

	private final EmailRepository emailRepository;
	private final ModelMapper modelMapper;

	public EmailService(EmailRepository emailRepository, ModelMapper modelMapper) {
		this.emailRepository = emailRepository;
		this.modelMapper = modelMapper;
	}

	// LISTAR POR ID
	public ResponseEntity<ResponseWrapper<EmailDTO>> findById(Long id) {
		Email email = emailRepository.findById(id).orElse(null);
		if (email != null) {
			return new ResponseEntity<>(new ResponseWrapper<>(this.modelMapper.map(email, EmailDTO.class), null),HttpStatus.OK);
		} else {
			ResponseWrapper<EmailDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Cadastro ID: " + id + " Não encontrado!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS
	public ResponseEntity<ResponseWrapper<List<EmailDTO>>> findAll() {
		List<EmailDTO> enderecos = emailRepository.findAll().stream().map(valorCC -> modelMapper.map(valorCC, EmailDTO.class)).collect(Collectors.toList());
		if(enderecos != null && !enderecos.isEmpty())
		{
			return new ResponseEntity<>(new ResponseWrapper<>(enderecos, null), HttpStatus.OK);
		}else {
			ResponseWrapper<List<EmailDTO>> responseWrapper = new ResponseWrapper<>();
	        responseWrapper.setMessage("Base de dados não tem nenhum usuário cadastrado");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS E MOSTRAR O RESULTADO UTILIZANDO PAGINAÇÃO
	public ResponseEntity<ResponseWrapper<Page<EmailDTO>>> findAll(Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Email> page = this.emailRepository.listAllByPages(pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<EmailDTO> dtoPage = page.map(item -> this.modelMapper.map(item, EmailDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		}else {
			ResponseWrapper<Page<EmailDTO>> responseWrapper = new ResponseWrapper<>();
	        responseWrapper.setMessage("Não foram encontrados endereços na base de dados");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}
}
