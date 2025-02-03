package br.com.gestao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.gestao.entities.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {
	
	Page<Email> findByClienteId(Long Id, PageRequest pageRequest);
	
	@Query("select e from Email e")
	Page<Email> listAllByPages(Pageable pageable);
}
