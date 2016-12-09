package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.AppApp;

import com.mycompany.myapp.domain.Fornecedor;
import com.mycompany.myapp.repository.FornecedorRepository;
import com.mycompany.myapp.repository.search.FornecedorSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FornecedorResource REST controller.
 *
 * @see FornecedorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppApp.class)
public class FornecedorResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";

    private static final String DEFAULT_CIDADE = "AAA";
    private static final String UPDATED_CIDADE = "BBB";

    @Inject
    private FornecedorRepository fornecedorRepository;

    @Inject
    private FornecedorSearchRepository fornecedorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restFornecedorMockMvc;

    private Fornecedor fornecedor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FornecedorResource fornecedorResource = new FornecedorResource();
        ReflectionTestUtils.setField(fornecedorResource, "fornecedorSearchRepository", fornecedorSearchRepository);
        ReflectionTestUtils.setField(fornecedorResource, "fornecedorRepository", fornecedorRepository);
        this.restFornecedorMockMvc = MockMvcBuilders.standaloneSetup(fornecedorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fornecedor createEntity(EntityManager em) {
        Fornecedor fornecedor = new Fornecedor()
                .nome(DEFAULT_NOME)
                .cidade(DEFAULT_CIDADE);
        return fornecedor;
    }

    @Before
    public void initTest() {
        fornecedorSearchRepository.deleteAll();
        fornecedor = createEntity(em);
    }

    @Test
    @Transactional
    public void createFornecedor() throws Exception {
        int databaseSizeBeforeCreate = fornecedorRepository.findAll().size();

        // Create the Fornecedor

        restFornecedorMockMvc.perform(post("/api/fornecedors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fornecedor)))
                .andExpect(status().isCreated());

        // Validate the Fornecedor in the database
        List<Fornecedor> fornecedors = fornecedorRepository.findAll();
        assertThat(fornecedors).hasSize(databaseSizeBeforeCreate + 1);
        Fornecedor testFornecedor = fornecedors.get(fornecedors.size() - 1);
        assertThat(testFornecedor.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testFornecedor.getCidade()).isEqualTo(DEFAULT_CIDADE);

        // Validate the Fornecedor in ElasticSearch
        Fornecedor fornecedorEs = fornecedorSearchRepository.findOne(testFornecedor.getId());
        assertThat(fornecedorEs).isEqualToComparingFieldByField(testFornecedor);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = fornecedorRepository.findAll().size();
        // set the field null
        fornecedor.setNome(null);

        // Create the Fornecedor, which fails.

        restFornecedorMockMvc.perform(post("/api/fornecedors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fornecedor)))
                .andExpect(status().isBadRequest());

        List<Fornecedor> fornecedors = fornecedorRepository.findAll();
        assertThat(fornecedors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCidadeIsRequired() throws Exception {
        int databaseSizeBeforeTest = fornecedorRepository.findAll().size();
        // set the field null
        fornecedor.setCidade(null);

        // Create the Fornecedor, which fails.

        restFornecedorMockMvc.perform(post("/api/fornecedors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fornecedor)))
                .andExpect(status().isBadRequest());

        List<Fornecedor> fornecedors = fornecedorRepository.findAll();
        assertThat(fornecedors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFornecedors() throws Exception {
        // Initialize the database
        fornecedorRepository.saveAndFlush(fornecedor);

        // Get all the fornecedors
        restFornecedorMockMvc.perform(get("/api/fornecedors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(fornecedor.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE.toString())));
    }

    @Test
    @Transactional
    public void getFornecedor() throws Exception {
        // Initialize the database
        fornecedorRepository.saveAndFlush(fornecedor);

        // Get the fornecedor
        restFornecedorMockMvc.perform(get("/api/fornecedors/{id}", fornecedor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fornecedor.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.cidade").value(DEFAULT_CIDADE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFornecedor() throws Exception {
        // Get the fornecedor
        restFornecedorMockMvc.perform(get("/api/fornecedors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFornecedor() throws Exception {
        // Initialize the database
        fornecedorRepository.saveAndFlush(fornecedor);
        fornecedorSearchRepository.save(fornecedor);
        int databaseSizeBeforeUpdate = fornecedorRepository.findAll().size();

        // Update the fornecedor
        Fornecedor updatedFornecedor = fornecedorRepository.findOne(fornecedor.getId());
        updatedFornecedor
                .nome(UPDATED_NOME)
                .cidade(UPDATED_CIDADE);

        restFornecedorMockMvc.perform(put("/api/fornecedors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFornecedor)))
                .andExpect(status().isOk());

        // Validate the Fornecedor in the database
        List<Fornecedor> fornecedors = fornecedorRepository.findAll();
        assertThat(fornecedors).hasSize(databaseSizeBeforeUpdate);
        Fornecedor testFornecedor = fornecedors.get(fornecedors.size() - 1);
        assertThat(testFornecedor.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testFornecedor.getCidade()).isEqualTo(UPDATED_CIDADE);

        // Validate the Fornecedor in ElasticSearch
        Fornecedor fornecedorEs = fornecedorSearchRepository.findOne(testFornecedor.getId());
        assertThat(fornecedorEs).isEqualToComparingFieldByField(testFornecedor);
    }

    @Test
    @Transactional
    public void deleteFornecedor() throws Exception {
        // Initialize the database
        fornecedorRepository.saveAndFlush(fornecedor);
        fornecedorSearchRepository.save(fornecedor);
        int databaseSizeBeforeDelete = fornecedorRepository.findAll().size();

        // Get the fornecedor
        restFornecedorMockMvc.perform(delete("/api/fornecedors/{id}", fornecedor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean fornecedorExistsInEs = fornecedorSearchRepository.exists(fornecedor.getId());
        assertThat(fornecedorExistsInEs).isFalse();

        // Validate the database is empty
        List<Fornecedor> fornecedors = fornecedorRepository.findAll();
        assertThat(fornecedors).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFornecedor() throws Exception {
        // Initialize the database
        fornecedorRepository.saveAndFlush(fornecedor);
        fornecedorSearchRepository.save(fornecedor);

        // Search the fornecedor
        restFornecedorMockMvc.perform(get("/api/_search/fornecedors?query=id:" + fornecedor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fornecedor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE.toString())));
    }
}
