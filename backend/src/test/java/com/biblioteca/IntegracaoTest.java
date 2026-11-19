package com.biblioteca;

import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private LivroRepository livros;
    @Autowired private AutorRepository autores;

    @BeforeEach
    @AfterEach
    void limpar() {
        livros.deleteAll();
        autores.deleteAll();
    }

    private long criarAutor(String nome) throws Exception {
        String json = mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.createObjectNode().put("nome", nome).toString()))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }

    private JsonNode criarLivro(String titulo, long autorId) throws Exception {
        var dados = mapper.createObjectNode().put("titulo", titulo).put("isbn", "123")
                .put("anoPublicacao", 1899).put("genero", "Romance");
        dados.putObject("autor").put("id", autorId);
        return mapper.readTree(mvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON).content(dados.toString()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.autor.id").value(autorId))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    void deveVincularLivroAAutor() throws Exception {
        long autorId = criarAutor("Machado de Assis");
        JsonNode livro = criarLivro("Dom Casmurro", autorId);
        long id = livro.get("id").asLong();
        assertThat(livro.get("criadoEm").asText()).isNotBlank();
        mvc.perform(get("/api/livros/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.autor.id").value(autorId))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));
        assertThat(livros.findByAutorId(autorId)).extracting(l -> l.getId()).containsExactly(id);

        long novoAutor = criarAutor("Outro autor");
        mvc.perform(put("/api/livros/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":999,\"titulo\":\"Atualizado\",\"autor\":{\"id\":" + novoAutor + "}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.autor.id").value(novoAutor))
                .andExpect(jsonPath("$.criadoEm").value(livro.get("criadoEm").asText()));
        mvc.perform(get("/api/livros/" + id)).andExpect(jsonPath("$.autor.id").value(novoAutor))
                .andExpect(jsonPath("$.isbn").isEmpty()).andExpect(jsonPath("$.anoPublicacao").isEmpty())
                .andExpect(jsonPath("$.genero").isEmpty());
        assertThat(livros.findByAutorId(autorId)).isEmpty();
        mvc.perform(delete("/api/livros/" + id)).andExpect(status().isOk());
        mvc.perform(get("/api/livros/" + id)).andExpect(status().isNotFound());
        assertThat(autores.count()).isEqualTo(2);
        mvc.perform(delete("/api/autores/" + autorId)).andExpect(status().isOk());
        mvc.perform(delete("/api/autores/" + novoAutor)).andExpect(status().isOk());
    }

    @Test
    void deveOrdenarLivrosPorTitulo() throws Exception {
        long autor = criarAutor("Autor");
        criarLivro("Zebra", autor);
        criarLivro("Abelha", autor);
        mvc.perform(get("/api/livros")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Abelha"))
                .andExpect(jsonPath("$[1].titulo").value("Zebra"));
    }
}
