package br.com.gestao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.gestao.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
	//Cliente.NOME LIKE '% NOME %'
	Page<Cliente> findByNomeLike(String nome, PageRequest pageRequest);
	
	@Query("select u from Usuario u")
	Page<Cliente> listAllByPages(Pageable pageable);
}
