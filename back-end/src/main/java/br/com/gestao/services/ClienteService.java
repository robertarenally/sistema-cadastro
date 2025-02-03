package br.com.gestao.services;

import java.util.List;
import java.util.regex.Pattern;
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
import br.com.gestao.dto.ClienteDTO;
import br.com.gestao.dto.EmailDTO;
import br.com.gestao.dto.EnderecoDTO;
import br.com.gestao.dto.TelefoneDTO;
import br.com.gestao.dto.ViaCepDTO;
import br.com.gestao.entities.Cliente;
import br.com.gestao.entities.Email;
import br.com.gestao.entities.Endereco;
import br.com.gestao.entities.Telefone;
import br.com.gestao.repositories.ClienteRepository;
import br.com.gestao.repositories.EmailRepository;
import br.com.gestao.repositories.EnderecoRepository;
import br.com.gestao.repositories.TelefoneRepository;

@Service
public class ClienteService {

	private final ClienteRepository clienteRepository;
	private final EnderecoRepository enderecoRepository;
	private final EmailRepository emailRepository;
	private final TelefoneRepository telefoneRepository;
	private final ModelMapper modelMapper;
	private final RestTemplate restTemplate;

	public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper,
			EnderecoRepository enderecoRepository, RestTemplate restTemplate, EmailRepository emailRepository,
			TelefoneRepository telefoneRepository) {
		this.clienteRepository = clienteRepository;
		this.enderecoRepository = enderecoRepository;
		this.emailRepository = emailRepository;
		this.telefoneRepository = telefoneRepository;
		this.modelMapper = modelMapper;
		this.restTemplate = restTemplate;
	}

	// LISTAR Cliente POR ID
	public ResponseEntity<ResponseWrapper<ClienteDTO>> findById(Long id) {
		Cliente cliente = clienteRepository.findById(id).orElse(null);
		if (cliente != null) {
			return new ResponseEntity<>(new ResponseWrapper<>(this.modelMapper.map(cliente, ClienteDTO.class), null),
					HttpStatus.OK);
		} else {
			ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Usuário não encontrado com o ID: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS
	public ResponseEntity<ResponseWrapper<List<ClienteDTO>>> findAll() {
		List<ClienteDTO> clientes = clienteRepository.findAll().stream()
				.map(Cliente -> modelMapper.map(Cliente, ClienteDTO.class)).collect(Collectors.toList());
		if (clientes != null && !clientes.isEmpty()) {
			return new ResponseEntity<>(new ResponseWrapper<>(clientes, null), HttpStatus.OK);
		} else {
			ResponseWrapper<List<ClienteDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Base de dados não tem nenhum usuário cadastrado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// ENDEREÇO DE UM CLIENTE
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> findEnderecoByIdCliente(Long id, Integer pagina,
			Integer quantidade) {

		Cliente cliente = this.clienteRepository.findById(id).orElse(null);

		if (cliente != null && cliente.getEndereco() != null) {
			EnderecoDTO enderecoDTO = this.modelMapper.map(cliente.getEndereco(), EnderecoDTO.class);
			return new ResponseEntity<>(new ResponseWrapper<>(enderecoDTO, null), HttpStatus.OK);
		} else {
			ResponseWrapper<EnderecoDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foi encontrado endereço para esse usuário");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS OS CLIENTES QUE TEM UM NOME PARECIDO COM O VALOR DO NOME
	// CONSULTADO
	public ResponseEntity<ResponseWrapper<Page<ClienteDTO>>> findByNomeLike(Integer pagina, Integer quantidade,
			String nome) {
		Sort sort = Sort.by("nome").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Cliente> page = this.clienteRepository.findByNomeLike("%" + nome + "%", pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<ClienteDTO> dtoPage = page.map(item -> this.modelMapper.map(item, ClienteDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<ClienteDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados usuários com esse nome");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// LISTAR TODOS E MOSTRAR O RESULTADO UTILIZANDO PAGINAÇÃO
	public ResponseEntity<ResponseWrapper<Page<ClienteDTO>>> findAll(Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Cliente> page = this.clienteRepository.listAllByPages(pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<ClienteDTO> dtoPage = page.map(item -> this.modelMapper.map(item, ClienteDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<ClienteDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados usuários na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}

	// SALVAR (INSERIR/ALTERAR) O CADASTRO DO CLIENTE, INCLUINDO O ENDERECO
	@Transactional
	public ResponseEntity<ResponseWrapper<ClienteDTO>> salvarCliente(final ClienteDTO ClienteDTO) {
		Cliente clienteSalvar = this.modelMapper.map(ClienteDTO, Cliente.class);
		Endereco enderecoSalvar = clienteSalvar.getEndereco();

		// Remove caracteres especiais do CEP
		enderecoSalvar.setCep(enderecoSalvar.getCep().replaceAll("[^0-9]", ""));

		// Consulta na API ViaCEP
		String url = "https://viacep.com.br/ws/" + enderecoSalvar.getCep() + "/json/";
		ResponseEntity<ViaCepDTO> response = restTemplate.getForEntity(url, ViaCepDTO.class);

		if (response.getBody() == null || response.getBody().getCep() == null) {
			ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("CEP inválido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
		}

		// Atualiza os dados do endereço com os retornados da API
		enderecoSalvar.setLogradouro(response.getBody().getLogradouro());
		enderecoSalvar.setBairro(response.getBody().getBairro());
		enderecoSalvar.setCidade(response.getBody().getLocalidade());
		enderecoSalvar.setUf(response.getBody().getUf());

		// Validação de telefones
		List<Telefone> telefones = clienteSalvar.getTelefones();
		if (telefones == null || telefones.isEmpty()) {
			ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Pelo menos um telefone deve ser cadastrado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
		}
		telefones.forEach(telefone -> telefone.setNumero(telefone.getNumero().replaceAll("[^0-9]", "")));
		// salva os telefones
		telefones.forEach(c -> c = this.telefoneRepository.save(c));

		// Validação de emails
		List<Email> emails = clienteSalvar.getEmails();
		if (emails == null || emails.isEmpty()) {
			ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Pelo menos um email deve ser cadastrado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
		}
		Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
		for (Email email : emails) {
			if (!emailPattern.matcher(email.getEmail()).matches()) {
				ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
				responseWrapper.setMessage("E-mail inválido");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
			}
		}
		// salva os e-mails
		emails.forEach(c -> c = this.emailRepository.save(c));

		// Salva o endereço do cliente
		enderecoSalvar = this.enderecoRepository.save(enderecoSalvar);

		if (enderecoSalvar != null && enderecoSalvar.getId() != null) {

			// atribui o id dos emails salvos ao cliente
			clienteSalvar.setEmails(emails);

			// atribui o id dos telefones salvos ao cliente
			clienteSalvar.setTelefones(telefones);

			// atribui o id do endereço salvo ao cliente
			clienteSalvar.setEndereco(enderecoSalvar);

			// retira os caracteres especiais do cpf do cliente
			clienteSalvar.setCpf(clienteSalvar.getCpf().replaceAll("[^0-9]", ""));

			// salva o cliente:
			clienteSalvar = this.clienteRepository.save(clienteSalvar);

			if (clienteSalvar != null && clienteSalvar.getId() != null) {
				return new ResponseEntity<>(
						new ResponseWrapper<>(this.modelMapper.map(clienteSalvar, ClienteDTO.class), null),
						HttpStatus.CREATED);
			} else {
				ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
				responseWrapper.setMessage("Erro ao cadastrar o cliente na base de dados");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
			}
		} else {
			ResponseWrapper<ClienteDTO> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Erro ao cadastrar o endereço do cliente na base de dados");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
		}
	}

	// LISTAR TODOS OS EMAILS DE UM CLIENTE
	public ResponseEntity<ResponseWrapper<Page<EmailDTO>>> findEmailsByIdCliente(Long id, Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Email> page = this.emailRepository.findByClienteId(id, pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<EmailDTO> dtoPage = page.map(item -> this.modelMapper.map(item, EmailDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<EmailDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados endereços para esse usuário");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}
	
	// LISTAR TODOS OS TELEFONES DE UM CLIENTE
	public ResponseEntity<ResponseWrapper<Page<TelefoneDTO>>> findTelefonesByIdCliente(Long id, Integer pagina, Integer quantidade) {
		Sort sort = Sort.by("id").ascending();
		PageRequest pageRequest = PageRequest.of(pagina - 1, quantidade, sort);

		Page<Telefone> page = this.telefoneRepository.findByClienteId(id, pageRequest);

		if (page != null && !page.isEmpty()) {
			Page<TelefoneDTO> dtoPage = page.map(item -> this.modelMapper.map(item, TelefoneDTO.class));
			return new ResponseEntity<>(new ResponseWrapper<>(dtoPage, null), HttpStatus.OK);
		} else {
			ResponseWrapper<Page<TelefoneDTO>> responseWrapper = new ResponseWrapper<>();
			responseWrapper.setMessage("Não foram encontrados endereços para esse usuário");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
		}
	}
}
