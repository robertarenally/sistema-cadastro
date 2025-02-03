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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import br.com.gestao.commons.ResponseWrapper;
import br.com.gestao.dto.EnderecoDTO;
import br.com.gestao.dto.ViaCepDTO;
import br.com.gestao.entities.Endereco;
import br.com.gestao.repositories.EnderecoRepository;

@Service
public class EnderecoService {

	private final EnderecoRepository enderecoRepository;
	private final ModelMapper modelMapper;
	private final RestTemplate restTemplate;

	public EnderecoService(EnderecoRepository enderecoRepository, ModelMapper modelMapper, RestTemplate restTemplate) {
		this.enderecoRepository = enderecoRepository;
		this.modelMapper = modelMapper;
		this.restTemplate = restTemplate;
	}

	// LISTAR POR ID
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> findById(Long id) {
		Endereco endereco = enderecoRepository.findById(id).orElse(null);
		if (endereco != null) {
			return new ResponseEntity<>(new ResponseWrapper<>(this.modelMapper.map(endereco, EnderecoDTO.class), null),
					HttpStatus.OK);
		} else {
			ResponseWrapper<EnderecoDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Cadastro ID: " + id + " Não encontrado!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS
	public ResponseEntity<ResponseWrapper<List<EnderecoDTO>>> findAll() {
		List<EnderecoDTO> enderecos = enderecoRepository.findAll().stream()
				.map(valorCC -> modelMapper.map(valorCC, EnderecoDTO.class)).collect(Collectors.toList());
		if (enderecos != null && !enderecos.isEmpty()) {
			return new ResponseEntity<>(new ResponseWrapper<>(enderecos, null), HttpStatus.OK);
		} else {
			ResponseWrapper<List<EnderecoDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Base de dados não tem nenhum usuário cadastrado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS OS ENDEREÇOS POR CEP
	public ResponseEntity<ResponseWrapper<Page<EnderecoDTO>>> findByCep(Integer pagina, Integer quantidade,
			String cep) {
		Sort sort = Sort.by("cep").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Endereco> page = this.enderecoRepository.findByCep(cep, pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<EnderecoDTO> dtoPage = page.map(item -> this.modelMapper.map(item, EnderecoDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<EnderecoDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados endereços para esse cep");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS E MOSTRAR O RESULTADO UTILIZANDO PAGINAÇÃO
	public ResponseEntity<ResponseWrapper<Page<EnderecoDTO>>> findAll(Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Endereco> page = this.enderecoRepository.listAllByPages(pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<EnderecoDTO> dtoPage = page.map(item -> this.modelMapper.map(item, EnderecoDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<EnderecoDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados endereços na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// ALTERAR O CADASTRO DE ENDERECO
	@Transactional
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> alterarEndereco(final EnderecoDTO enderecoDTO) {
		Endereco enderecoSalvar = this.modelMapper.map(enderecoDTO, Endereco.class);

		if (enderecoSalvar.getId() == null) {
			ResponseWrapper<EnderecoDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Endereço não possui id válido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
		} else {
			// Remove caracteres especiais do CEP
			enderecoSalvar.setCep(enderecoSalvar.getCep().replaceAll("[^0-9]", ""));

			// Consulta na API ViaCEP
			String url = "https://viacep.com.br/ws/" + enderecoSalvar.getCep() + "/json/";
			ResponseEntity<ViaCepDTO> response = restTemplate.getForEntity(url, ViaCepDTO.class);

			if (response.getBody() == null || response.getBody().getCep() == null) {
				ResponseWrapper<EnderecoDTO> responseWrapper = new ResponseWrapper<>();
				responseWrapper.setMessage("CEP inválido");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
			}

			// Atualiza os dados do endereço com os retornados da API
			enderecoSalvar.setLogradouro(response.getBody().getLogradouro());
			enderecoSalvar.setBairro(response.getBody().getBairro());
			enderecoSalvar.setCidade(response.getBody().getLocalidade());
			enderecoSalvar.setUf(response.getBody().getUf());

			// Salva o endereço do cliente
			enderecoSalvar = this.enderecoRepository.save(enderecoSalvar);
			return new ResponseEntity<>(
					new ResponseWrapper<>(this.modelMapper.map(enderecoSalvar, EnderecoDTO.class), null),
					HttpStatus.CREATED);
		}

	}
}
