package br.com.gestao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import br.com.gestao.commons.Const;
import br.com.gestao.enums.TipoTelefone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Const.TB_TELEFONE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {

	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "TIPO_TELEFONE", nullable = false)
    private TipoTelefone tipo;
	
	@Column(name = "NR_TELEFONE",nullable = false, length = 11)
    private String numero; 

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;
	
	//Normalização do numero de telefone Antes de Salvar
	@PrePersist
	void prePersist() {
		if(this.numero != null) 
			this.numero = numero.replaceAll("[^a-zA-Z0-9 ]", "");
	}

	//Normalização do numero de telefon Antes de Atualizar
	@PreUpdate
	void preUpdate() {
		if(this.numero != null)
			this.numero = numero.replaceAll("[^a-zA-Z0-9 ]", "");
	}
}
