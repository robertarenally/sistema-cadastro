package br.com.gestao.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestao.commons.Const;
import br.com.gestao.commons.ResponseWrapper;
import br.com.gestao.dto.ClienteDTO;
import br.com.gestao.dto.EmailDTO;
import br.com.gestao.dto.EnderecoDTO;
import br.com.gestao.dto.TelefoneDTO;
import br.com.gestao.services.ClienteService;
import br.com.gestao.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/clientes")
@Tag(name = "ClienteController", description = "Operações no cadastro e consulta dos dados do Cliente")
public class ClienteController {

	private final ClienteService usuarioService;
	private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    
	public ClienteController(ClienteService usuarioService, TokenService tokenService, UserDetailsService userDetailsService) {
		this.usuarioService = usuarioService;
		this.userDetailsService = userDetailsService;
		this.tokenService = tokenService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Pesquisar por ID do cadastro de Cliente")
	public ResponseEntity<ResponseWrapper<ClienteDTO>> findById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		return usuarioService.findById(id);
	}

	@GetMapping
	@Operation(summary = "Listar todos os cadastros de Clientes")
	public ResponseEntity<ResponseWrapper<List<ClienteDTO>>> findAll(@RequestHeader("Authorization") String token) {
		return usuarioService.findAll();
	}
	
	@GetMapping("/{id}/endereco")
	@Operation(summary = "Get endereço do cliente")
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> findById(@PathVariable Long id,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade, @RequestHeader("Authorization") String token) {
		return usuarioService.findEnderecoByIdCliente(id, pagina, quantidade);
	}
	
	@Operation(summary = "Listar todos os Clientes e mostrar o resultado utilizando paginação")
	@GetMapping(value = "/listar-todos", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<ClienteDTO>>> findAll(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade, @RequestHeader("Authorization") String token) {
		return this.usuarioService.findAll(pagina, quantidade);
	}
	
	@Operation(summary = "Listar todos os Cliente que tem um nome parecido com o Nome consultado")
	@GetMapping(value = "/listar-por-nome", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<ClienteDTO>>> findByNomeLike(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade,
			@RequestParam(value = "name", required = false, defaultValue = "") String nome,
			@RequestHeader("Authorization") String token) {
		return this.usuarioService.findByNomeLike(pagina, quantidade, nome);
	}
	
	@Operation(summary = "Salvar um cadastro completo do Cliente, incluindo endereço, emails e telefones")
	@PostMapping(value = "/salvar", headers = { Const.HEADER_ACCEPT_JSON }, produces = Const.JSON_TYPE, consumes = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<ClienteDTO>> salvarUsuario(@RequestBody ClienteDTO body, @RequestHeader("Authorization") String token) {
	    // Extrai o username do token
	    String username = tokenService.extrairUsername(token);
	    
	    // Obtém os detalhes do usuário autenticado
	    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	    
	    // Verifica se o usuário tem a ROLE_ADMIN
	    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return usuarioService.salvarCliente(body);
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>(null, "Acesso negado"));
	    }
	}


	@Operation(summary = "Alterar um cadastro completo do Usuario, incluindo endereço, emails e telefones")
	@PutMapping(value = "/alterar", headers = { Const.HEADER_ACCEPT_JSON }, produces = Const.JSON_TYPE, consumes = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<ClienteDTO>> updateUsuario(@RequestBody ClienteDTO body, @RequestHeader("Authorization") String token) {
		// Extrai o username do token
	    String username = tokenService.extrairUsername(token);
	    
	    // Obtém os detalhes do usuário autenticado
	    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	    
	    // Verifica se o usuário tem a ROLE_ADMIN
	    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return usuarioService.salvarCliente(body);
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>(null, "Acesso negado"));
	    }
	}
	

	@GetMapping("/{id}/emails")
	@Operation(summary = "Listar todos os emails de um cliente")
	public ResponseEntity<ResponseWrapper<Page<EmailDTO>>> findEmailsById(@PathVariable Long id,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade, @RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return usuarioService.findEmailsByIdCliente(id, pagina, quantidade);
	}
	
	@GetMapping("/{id}/telefones")
	@Operation(summary = "Listar todos os telefones de um cliente")
	public ResponseEntity<ResponseWrapper<Page<TelefoneDTO>>> findTelefonesById(@PathVariable Long id,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade) {
		return usuarioService.findTelefonesByIdCliente(id, pagina, quantidade);
	}
}
