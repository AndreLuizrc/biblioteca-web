package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findAllByOrderByTituloAsc();
    List<Livro> findByAutorId(Long autorId);
}
