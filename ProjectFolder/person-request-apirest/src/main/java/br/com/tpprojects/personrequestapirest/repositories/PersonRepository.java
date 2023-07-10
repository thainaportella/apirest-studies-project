package br.com.tpprojects.personrequestapirest.repositories;

import br.com.tpprojects.personrequestapirest.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository tá depreciado, nao precisa mais usar
public interface PersonRepository extends JpaRepository<Person, Long> {}
