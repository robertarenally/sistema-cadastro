package br.com.gestao.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import br.com.gestao.commons.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Const.TB_CLIENTE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

	@Id
	@GeneratedValue
	@Column(name = "ID_USUARIO", nullable = false, unique = true)
	private Long id;

	@Column(name = "DS_NOME", length = 200, nullable = true)
	private String nome;
	
	@Column(name = "DS_CPF", nullable = false, unique = true, length = 14)
    private String cpf; 

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;
	
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Email> emails;
	
	// Normalização do CPF Antes de Salvar
	@PrePersist
	void prePersist() {
		if (this.cpf != null)
			this.cpf = cpf.replaceAll("[^a-zA-Z0-9 ]", "");
	}

	// Normalização do CPF Antes de Atualizar
	@PreUpdate
	void preUpdate() {
		if (this.cpf != null)
			this.cpf = cpf.replaceAll("[^a-zA-Z0-9 ]", "");
	}
}
