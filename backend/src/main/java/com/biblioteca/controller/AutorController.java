package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController {
    private final AutorRepository repository;

    public AutorController(AutorRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Autor> criar(@Valid @RequestBody Autor autor) {
        autor.setId(null);
        Autor salvo = repository.save(autor);
        return ResponseEntity.created(URI.create("/api/autores/" + salvo.getId())).body(salvo);
    }

    @GetMapping
    public List<Autor> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscar(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> atualizar(@PathVariable Long id, @Valid @RequestBody Autor dados) {
        return repository.findById(id).map(autor -> {
            autor.setNome(dados.getNome());
            autor.setNacionalidade(dados.getNacionalidade());
            autor.setDataNascimento(dados.getDataNascimento());
            return ResponseEntity.ok(repository.save(autor));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id).map(autor -> {
            repository.delete(autor);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
