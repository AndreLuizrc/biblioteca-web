package com.biblioteca.repository;

import com.biblioteca.model.Autor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findAllByOrderByNomeAsc();
}
