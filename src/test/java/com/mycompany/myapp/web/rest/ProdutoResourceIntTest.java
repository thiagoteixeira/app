package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.AppApp;

import com.mycompany.myapp.domain.Produto;
import com.mycompany.myapp.domain.Fornecedor;
import com.mycompany.myapp.repository.ProdutoRepository;
import com.mycompany.myapp.repository.search.ProdutoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProdutoResource REST controller.
 *
 * @see ProdutoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppApp.class)
public class ProdutoResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(1);

    @Inject
    private ProdutoRepository produtoRepository;

    @Inject
    private ProdutoSearchRepository produtoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProdutoMockMvc;

    private Produto produto;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProdutoResource produtoResource = new ProdutoResource();
        ReflectionTestUtils.setField(produtoResource, "produtoSearchRepository", produtoSearchRepository);
        ReflectionTestUtils.setField(produtoResource, "produtoRepository", produtoRepository);
        this.restProdutoMockMvc = MockMvcBuilders.standaloneSetup(produtoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produto createEntity(EntityManager em) {
        Produto produto = new Produto()
                .nome(DEFAULT_NOME)
                .valor(DEFAULT_VALOR);
        // Add required entity
        Fornecedor fornecedor = FornecedorResourceIntTest.createEntity(em);
        em.persist(fornecedor);
        em.flush();
        produto.setFornecedor(fornecedor);
        return produto;
    }

    @Before
    public void initTest() {
        produtoSearchRepository.deleteAll();
        produto = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduto() throws Exception {
        int databaseSizeBeforeCreate = produtoRepository.findAll().size();

        // Create the Produto

        restProdutoMockMvc.perform(post("/api/produtos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(produto)))
                .andExpect(status().isCreated());

        // Validate the Produto in the database
        List<Produto> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(databaseSizeBeforeCreate + 1);
        Produto testProduto = produtos.get(produtos.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testProduto.getValor()).isEqualTo(DEFAULT_VALOR);

        // Validate the Produto in ElasticSearch
        Produto produtoEs = produtoSearchRepository.findOne(testProduto.getId());
        assertThat(produtoEs).isEqualToComparingFieldByField(testProduto);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = produtoRepository.findAll().size();
        // set the field null
        produto.setNome(null);

        // Create the Produto, which fails.

        restProdutoMockMvc.perform(post("/api/produtos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(produto)))
                .andExpect(status().isBadRequest());

        List<Produto> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValorIsRequired() throws Exception {
        int databaseSizeBeforeTest = produtoRepository.findAll().size();
        // set the field null
        produto.setValor(null);

        // Create the Produto, which fails.

        restProdutoMockMvc.perform(post("/api/produtos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(produto)))
                .andExpect(status().isBadRequest());

        List<Produto> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProdutos() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtos
        restProdutoMockMvc.perform(get("/api/produtos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(produto.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())));
    }

    @Test
    @Transactional
    public void getProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get the produto
        restProdutoMockMvc.perform(get("/api/produtos/{id}", produto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(produto.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProduto() throws Exception {
        // Get the produto
        restProdutoMockMvc.perform(get("/api/produtos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);
        produtoSearchRepository.save(produto);
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();

        // Update the produto
        Produto updatedProduto = produtoRepository.findOne(produto.getId());
        updatedProduto
                .nome(UPDATED_NOME)
                .valor(UPDATED_VALOR);

        restProdutoMockMvc.perform(put("/api/produtos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProduto)))
                .andExpect(status().isOk());

        // Validate the Produto in the database
        List<Produto> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(databaseSizeBeforeUpdate);
        Produto testProduto = produtos.get(produtos.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProduto.getValor()).isEqualTo(UPDATED_VALOR);

        // Validate the Produto in ElasticSearch
        Produto produtoEs = produtoSearchRepository.findOne(testProduto.getId());
        assertThat(produtoEs).isEqualToComparingFieldByField(testProduto);
    }

    @Test
    @Transactional
    public void deleteProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);
        produtoSearchRepository.save(produto);
        int databaseSizeBeforeDelete = produtoRepository.findAll().size();

        // Get the produto
        restProdutoMockMvc.perform(delete("/api/produtos/{id}", produto.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean produtoExistsInEs = produtoSearchRepository.exists(produto.getId());
        assertThat(produtoExistsInEs).isFalse();

        // Validate the database is empty
        List<Produto> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);
        produtoSearchRepository.save(produto);

        // Search the produto
        restProdutoMockMvc.perform(get("/api/_search/produtos?query=id:" + produto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())));
    }
}
