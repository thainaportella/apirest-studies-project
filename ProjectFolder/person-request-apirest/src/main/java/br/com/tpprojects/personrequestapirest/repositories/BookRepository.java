package br.com.tpprojects.personrequestapirest.repositories;

import br.com.tpprojects.personrequestapirest.model.Book;
import br.com.tpprojects.personrequestapirest.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Integer> {}
