package br.com.gestao.dto;

import java.util.List;

import br.com.gestao.entities.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Cliente")
public class ClienteDTO {

	private Long id;

	private String nome;

    private String cpf; 

    private Endereco endereco;

    private List<TelefoneDTO> telefones;

    private List<EmailDTO> emails;
}
