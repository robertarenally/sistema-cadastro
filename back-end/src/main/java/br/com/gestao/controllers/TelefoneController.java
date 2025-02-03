package br.com.gestao.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestao.commons.Const;
import br.com.gestao.commons.ResponseWrapper;
import br.com.gestao.dto.TelefoneDTO;
import br.com.gestao.services.TelefoneService;
import br.com.gestao.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/telefones")
@Tag(name = "TelefoneController", description = "Operações de consulta no telefone")
public class TelefoneController {

	private final TelefoneService telefoneService;
	private final TokenService tokenService;

	public TelefoneController(TelefoneService telefoneService, TokenService tokenService) {
		this.telefoneService = telefoneService;
		this.tokenService = tokenService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Pesquisar por ID do cadastro de Telefones")
	public ResponseEntity<ResponseWrapper<TelefoneDTO>> findById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return telefoneService.findById(id);
	}

	@GetMapping
	@Operation(summary = "Listar todos os cadastros de Telefones")
	public ResponseEntity<ResponseWrapper<List<TelefoneDTO>>> findAll(@RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return telefoneService.findAll();
	}
	
	@Operation(summary = "Listar todos os Telefones e mostrar o resultado utilizando paginação")
	@GetMapping(value = "/listar-todos", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<TelefoneDTO>>> findAll(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade,
			@RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return this.telefoneService.findAll(pagina, quantidade);
	}
}
