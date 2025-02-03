package br.com.gestao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.gestao.commons.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Const.TB_EMAIL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;
	
	@Column(name = "DS_EMAIL",nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;
}
