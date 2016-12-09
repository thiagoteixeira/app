package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Produto;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Produto entity.
 */
@SuppressWarnings("unused")
public interface ProdutoRepository extends JpaRepository<Produto,Long> {

}
