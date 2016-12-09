package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Produto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Produto entity.
 */
public interface ProdutoSearchRepository extends ElasticsearchRepository<Produto, Long> {
}
