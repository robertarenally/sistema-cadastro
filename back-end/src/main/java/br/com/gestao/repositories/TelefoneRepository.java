package br.com.gestao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.gestao.entities.Telefone;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
	
	Page<Telefone> findByClienteId(Long Id, PageRequest pageRequest);
	
	@Query("select t from Telefone t")
	Page<Telefone> listAllByPages(Pageable pageable);
}
