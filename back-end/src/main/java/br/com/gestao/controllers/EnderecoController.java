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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestao.commons.Const;
import br.com.gestao.commons.ResponseWrapper;
import br.com.gestao.dto.EnderecoDTO;
import br.com.gestao.services.EnderecoService;
import br.com.gestao.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/enderecos")
@Tag(name = "EnderecoController", description = "Operações de consulta no Endereco")
public class EnderecoController {

	private final EnderecoService enderecoService;
	private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

	public EnderecoController(EnderecoService enderecoService, UserDetailsService userDetailsService, TokenService tokenService) {
		this.enderecoService = enderecoService;
		this.userDetailsService = userDetailsService;
		this.tokenService = tokenService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Pesquisar por ID do cadastro de Endereco")
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> findById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		return enderecoService.findById(id);
	}

	@GetMapping
	@Operation(summary = "Listar todos os cadastros de Endereco")
	public ResponseEntity<ResponseWrapper<List<EnderecoDTO>>> findAll(@RequestHeader("Authorization") String token) {
		return enderecoService.findAll();
	}
	
	@Operation(summary = "Listar todos os endereços por cep")
	@GetMapping(value = "/listar-por-cep", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<EnderecoDTO>>> findByCep(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade,
			@RequestParam(value = "cep", required = false, defaultValue = "") String cep,
			@RequestHeader("Authorization") String token) {
		return this.enderecoService.findByCep(pagina, quantidade, cep);
	}
	
	@Operation(summary = "Listar todos os endereços e mostrar o resultado utilizando paginação")
	@GetMapping(value = "/listar-todos", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<EnderecoDTO>>> findAll(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade,
			@RequestHeader("Authorization") String token) {
		return this.enderecoService.findAll(pagina, quantidade);
	}
	
	@Operation(summary = "Alterar o endereço")
	@PutMapping(value = "/alterar", headers = { Const.HEADER_ACCEPT_JSON }, produces = Const.JSON_TYPE, consumes = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<EnderecoDTO>> salvarEndereco(@RequestBody EnderecoDTO endereco,@RequestHeader("Authorization") String token) {
		// Extrai o username do token
	    String username = tokenService.extrairUsername(token);
	    
	    // Obtém os detalhes do usuário autenticado
	    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	    
	    // Verifica se o usuário tem a ROLE_ADMIN
	    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return enderecoService.alterarEndereco(endereco);
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>(null, "Acesso negado"));
	    }
	}
}
