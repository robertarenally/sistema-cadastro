package br.com.gestao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import br.com.gestao.commons.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Const.TB_ENDERECO)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;
	
	@Column(name = "CEP", length = 8, nullable = false)
	private String cep;
	
	@Column(name = "LOGRADOURO", length = 200, nullable = true)
	private String logradouro;
	
	@Column(name = "BAIRRO", nullable = false, length = 50)
    private String bairro;
	
	@Column(name = "CIDADE", length = 200, nullable = true)
	private String cidade;
	
	@Column(name = "UF", length = 200, nullable = true)
	private String uf;
	
	@Column(name = "numero", length = 3, nullable = true)
	private String numero;
	
	//Normalização do CEP Antes de Salvar
	@PrePersist
	void prePersist() {
		if(this.cep != null) 
			this.cep = cep.replaceAll("[^a-zA-Z0-9 ]", "");
	}

	//Normalização do CEP Antes de Atualizar
	@PreUpdate
	void preUpdate() {
		if(this.cep != null)
			this.cep = cep.replaceAll("[^a-zA-Z0-9 ]", "");
	}
}
