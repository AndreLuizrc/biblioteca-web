package com.biblioteca;

import com.biblioteca.repository.AutorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AutorPersistenciaTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private AutorRepository repository;

    @BeforeEach
    void limparDados() { repository.deleteAll(); }

    @Test
    void devePersistirFluxoCompletoDeAutorComOrdenacao() throws Exception {
        JsonNode criado = mapper.readTree(mvc.perform(post("/api/autores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Zelia\",\"dataNascimento\":\"1910-07-02\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.criadoEm").isNotEmpty())
                .andReturn().getResponse().getContentAsString());
        String url = "/api/autores/" + criado.get("id").asLong();
        mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Ana\"}")).andExpect(status().isCreated());
        mvc.perform(get("/api/autores")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"))
                .andExpect(jsonPath("$[1].nome").value("Zelia"));
        mvc.perform(get(url)).andExpect(status().isOk())
                .andExpect(jsonPath("$.dataNascimento").value("1910-07-02"));
        mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Zelia Gattai\",\"nacionalidade\":\"Brasileira\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Zelia Gattai"));
        JsonNode atualizado = mapper.readTree(mvc.perform(get(url)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertThat(atualizado.get("criadoEm")).isEqualTo(criado.get("criadoEm"));
        assertThat(atualizado.get("dataNascimento").isNull()).isTrue();
        mvc.perform(delete(url)).andExpect(status().isOk());
        mvc.perform(get(url)).andExpect(status().isNotFound());
        assertThat(repository.existsById(criado.get("id").asLong())).isFalse();
    }
}
