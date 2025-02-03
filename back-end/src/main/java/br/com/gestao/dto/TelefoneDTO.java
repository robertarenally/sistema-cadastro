package br.com.gestao.dto;

import br.com.gestao.enums.TipoTelefone;
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
@Schema(name = "Telefone")
public class TelefoneDTO {

	private Long id;

    private TipoTelefone tipo;

    private String numero; 

    private ClienteDTO cliente;
}
