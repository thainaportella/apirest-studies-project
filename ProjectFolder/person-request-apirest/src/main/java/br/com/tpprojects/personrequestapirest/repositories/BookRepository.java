package br.com.tpprojects.personrequestapirest.repositories;

import br.com.tpprojects.personrequestapirest.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {}
