package com.biblioteca.model;

import com.biblioteca.persistence.LocalDateType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 50, message = "A nacionalidade deve ter no máximo 50 caracteres.")
    @Column(length = 50)
    private String nacionalidade;

    @Column(name = "data_nascimento")
    @Type(LocalDateType.class)
    private LocalDate dataNascimento;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void preencherCriadoEm() {
        criadoEm = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
