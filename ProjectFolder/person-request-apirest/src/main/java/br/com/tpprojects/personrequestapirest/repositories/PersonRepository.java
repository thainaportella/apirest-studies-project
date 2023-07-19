package br.com.tpprojects.personrequestapirest.repositories;

import br.com.tpprojects.personrequestapirest.model.Person;
import br.com.tpprojects.personrequestapirest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// @Repository tá depreciado, nao precisa mais usar
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Modifying
    @Query("UPDATE Person p SET p.enabled = false WHERE p.id =:id")
    void disablePerson(@Param("id") Long id);
}
