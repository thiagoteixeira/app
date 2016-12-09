package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Fornecedor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Fornecedor entity.
 */
public interface FornecedorSearchRepository extends ElasticsearchRepository<Fornecedor, Long> {
}
