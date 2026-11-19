package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import java.time.LocalDate;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private AutorRepository repository;

    @Test
    void deveCriarComSucesso() throws Exception {
        when(repository.save(any(Autor.class))).thenAnswer(invocation -> {
            Autor autor = invocation.getArgument(0);
            assertThat(autor.getId()).isNull();
            autor.setId(1L);
            return autor;
        });

        mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":999,"nome":"Machado de Assis","nacionalidade":"Brasileira",
                     "dataNascimento":"1839-06-21"}
                    """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/autores/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"))
                .andExpect(jsonPath("$.dataNascimento").value("1839-06-21"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void deveRetornar404ParaIdInexistente(String metodo) throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(request(HttpMethod.valueOf(metodo), "/api/autores/999")
                .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Autor\"}"))
                .andExpect(status().isNotFound());
        verify(repository, never()).save(any());
        verify(repository, never()).delete(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"nome\":\"   \"}",
        "{\"nome\":\"Autor\",\"dataNascimento\":\"data-invalida\"}"})
    void deveRejeitarDadosInvalidos(String json) throws Exception {
        mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarCamposAcimaDoLimite() throws Exception {
        for (String json : List.of("{\"nome\":\"" + "A".repeat(101) + "\"}",
                "{\"nome\":\"Autor\",\"nacionalidade\":\"" + "B".repeat(51) + "\"}")) {
            mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarAutoresOrdenados() throws Exception {
        Autor primeiro = new Autor(); primeiro.setNome("Ana");
        Autor segundo = new Autor(); segundo.setNome("Zelia");
        when(repository.findAllByOrderByNomeAsc()).thenReturn(List.of(primeiro, segundo));

        mvc.perform(get("/api/autores")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"))
                .andExpect(jsonPath("$[1].nome").value("Zelia"));
        verify(repository).findAllByOrderByNomeAsc();
    }

    @Test
    void deveBuscarAutorExistente() throws Exception {
        Autor autor = new Autor(); autor.setId(1L); autor.setNome("Machado");
        when(repository.findById(1L)).thenReturn(Optional.of(autor));
        mvc.perform(get("/api/autores/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Machado"));
    }

    @Test
    void deveAtualizarSemAlterarIdOuDataDeCriacao() throws Exception {
        Autor autor = new Autor();
        autor.setId(1L); autor.setNome("Anterior");
        autor.setNacionalidade("Brasileira"); autor.setDataNascimento(LocalDate.of(1900, 1, 1));
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 19, 10, 0);
        autor.setCriadoEm(criadoEm);
        when(repository.findById(1L)).thenReturn(Optional.of(autor));
        when(repository.save(autor)).thenReturn(autor);

        mvc.perform(put("/api/autores/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":999,\"nome\":\"Atualizado\",\"criadoEm\":\"2000-01-01T00:00:00\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Atualizado"));
        assertThat(autor.getCriadoEm()).isEqualTo(criadoEm);
        assertThat(autor.getNacionalidade()).isNull();
        assertThat(autor.getDataNascimento()).isNull();
    }

    @Test
    void deveExcluirComStatus200() throws Exception {
        Autor autor = new Autor(); autor.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(autor));
        mvc.perform(delete("/api/autores/1")).andExpect(status().isOk());
        verify(repository).delete(autor);
    }
}
