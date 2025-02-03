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
import br.com.gestao.dto.EmailDTO;
import br.com.gestao.services.EmailService;
import br.com.gestao.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/emails")
@Tag(name = "EmailController", description = "Operações de consulta no Email")
public class EmailController {

	private final EmailService emailService;
	private final TokenService tokenService;

	public EmailController(EmailService emailService, TokenService tokenService) {
		this.emailService = emailService;
		this.tokenService = tokenService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Pesquisar por ID do cadastro de Emails")
	public ResponseEntity<ResponseWrapper<EmailDTO>> findById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return emailService.findById(id);
	}

	@GetMapping
	@Operation(summary = "Listar todos os cadastros de Emails")
	public ResponseEntity<ResponseWrapper<List<EmailDTO>>> findAll(@RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return emailService.findAll();
	}
	
	@Operation(summary = "Listar todos os Emails e mostrar o resultado utilizando paginação")
	@GetMapping(value = "/listar-todos", produces = Const.JSON_TYPE)
	public ResponseEntity<ResponseWrapper<Page<EmailDTO>>> findAll(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "quantity", required = false, defaultValue = "50") Integer quantidade,
			@RequestHeader("Authorization") String token) {
		if (!tokenService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(null, "Token inválido ou expirado"));
        }
		return this.emailService.findAll(pagina, quantidade);
	}
}
