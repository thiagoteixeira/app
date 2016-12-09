package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Fornecedor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Fornecedor entity.
 */
@SuppressWarnings("unused")
public interface FornecedorRepository extends JpaRepository<Fornecedor,Long> {

}
