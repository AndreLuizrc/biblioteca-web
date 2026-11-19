package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {
    private final LivroRepository repository;
    private final AutorRepository autores;

    public LivroController(LivroRepository repository, AutorRepository autores) {
        this.repository = repository;
        this.autores = autores;
    }

    private Autor resolverAutor(Livro dados) {
        if (dados.getAutor() == null || dados.getAutor().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID do autor.");
        }
        return autores.findById(dados.getAutor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor não encontrado."));
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        livro.setAutor(resolverAutor(livro));
        livro.setId(null);
        Livro salvo = repository.save(livro);
        return ResponseEntity.created(URI.create("/api/livros/" + salvo.getId())).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Livro>> listar() {
        return ResponseEntity.ok(repository.findAllByOrderByTituloAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        return repository.findById(id).map(livro -> {
            Autor autor = resolverAutor(dados);
            livro.setTitulo(dados.getTitulo());
            livro.setIsbn(dados.getIsbn());
            livro.setAnoPublicacao(dados.getAnoPublicacao());
            livro.setGenero(dados.getGenero());
            livro.setAutor(autor);
            return ResponseEntity.ok(repository.save(livro));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(livro -> {
            repository.delete(livro);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
