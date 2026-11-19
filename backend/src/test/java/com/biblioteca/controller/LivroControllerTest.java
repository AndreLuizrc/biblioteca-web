package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private LivroRepository repository;
    @MockBean private AutorRepository autores;

    private Autor autor(Long id) {
        Autor autor = new Autor(); autor.setId(id); autor.setNome("Autor " + id);
        return autor;
    }

    @Test
    void deveCriarComAutorPersistido() throws Exception {
        Autor autor = autor(1L);
        when(autores.findById(1L)).thenReturn(Optional.of(autor));
        when(repository.save(any())).thenAnswer(invocation -> {
            Livro livro = invocation.getArgument(0);
            assertThat(livro.getId()).isNull();
            assertThat(livro.getCriadoEm()).isNull();
            assertThat(livro.getAutor()).isSameAs(autor);
            livro.setId(7L);
            return livro;
        });
        mvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON).content("""
                {"id":999,"titulo":"Dom Casmurro","autor":{"id":1,"nome":"Ignorado"},
                 "criadoEm":"2000-01-01T00:00:00"}
                """))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/livros/7"))
                .andExpect(jsonPath("$.autor.nome").value("Autor 1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void deveRetornar404ParaLivroInexistente(String metodo) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(metodo), "/api/livros/999")
                .contentType(MediaType.APPLICATION_JSON).content("{\"titulo\":\"Livro\",\"autor\":{\"id\":1}}"))
                .andExpect(status().isNotFound());
        verify(repository, never()).save(any());
        verify(repository, never()).delete(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"titulo\":\"Livro\"}", "{\"titulo\":\"Livro\",\"autor\":{}}",
            "{\"titulo\":\"  \",\"autor\":{\"id\":1}}"})
    void deveRejeitarDadosInvalidos(String json) throws Exception {
        mvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT"})
    void deveRejeitarAutorInexistente(String metodo) throws Exception {
        Livro existente = new Livro(); existente.setTitulo("Anterior");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        mvc.perform(request(HttpMethod.valueOf(metodo), metodo.equals("POST") ? "/api/livros" : "/api/livros/1")
                .contentType(MediaType.APPLICATION_JSON).content("{\"titulo\":\"Livro\",\"autor\":{\"id\":999}}"))
                .andExpect(status().isNotFound());
        assertThat(existente.getTitulo()).isEqualTo("Anterior");
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarCamposAcimaDoLimite() throws Exception {
        for (String campo : List.of("titulo", "isbn", "genero")) {
            int limite = campo.equals("titulo") ? 200 : campo.equals("isbn") ? 20 : 50;
            String json = "{\"autor\":{\"id\":1}," + (campo.equals("titulo") ? "" : "\"titulo\":\"Livro\",")
                    + "\"" + campo + "\":\"" + "A".repeat(limite + 1) + "\"}";
            mvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }
        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarAutorELimparOpcionaisPreservandoIdentidade() throws Exception {
        Livro livro = new Livro(); livro.setId(7L); livro.setAutor(autor(1L));
        livro.setIsbn("123"); livro.setGenero("Romance"); livro.setAnoPublicacao(1899);
        LocalDateTime criado = LocalDateTime.of(2026, 11, 19, 10, 0); livro.setCriadoEm(criado);
        when(repository.findById(7L)).thenReturn(Optional.of(livro));
        when(autores.findById(2L)).thenReturn(Optional.of(autor(2L)));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        mvc.perform(put("/api/livros/7").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":999,\"titulo\":\"Atualizado\",\"autor\":{\"id\":2}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.autor.id").value(2));
        assertThat(livro.getCriadoEm()).isEqualTo(criado);
        assertThat(livro.getIsbn()).isNull(); assertThat(livro.getGenero()).isNull();
        assertThat(livro.getAnoPublicacao()).isNull();
    }

    @Test
    void deveListarEBuscarLivros() throws Exception {
        Livro livro = new Livro(); livro.setId(7L); livro.setTitulo("Livro"); livro.setAutor(autor(1L));
        when(repository.findAllByOrderByTituloAsc()).thenReturn(List.of(livro));
        when(repository.findById(7L)).thenReturn(Optional.of(livro));
        mvc.perform(get("/api/livros")).andExpect(status().isOk()).andExpect(jsonPath("$[0].autor.nome").value("Autor 1"));
        mvc.perform(get("/api/livros/7")).andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Livro"));
        verify(repository).findAllByOrderByTituloAsc();
    }

    @Test
    void deveExcluirComStatus200() throws Exception {
        Livro livro = new Livro(); livro.setId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(livro));
        mvc.perform(delete("/api/livros/7")).andExpect(status().isOk());
        verify(repository).delete(livro);
        verifyNoInteractions(autores);
    }
}
