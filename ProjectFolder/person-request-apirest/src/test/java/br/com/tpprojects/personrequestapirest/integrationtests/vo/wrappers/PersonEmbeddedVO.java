package br.com.tpprojects.personrequestapirest.integrationtests.vo.wrappers;

import br.com.tpprojects.personrequestapirest.integrationtests.vo.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PersonEmbeddedVO implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("personVOList")
    private List<PersonVO> persons;

    public PersonEmbeddedVO() {
    }

    public List<PersonVO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonVO> persons) {
        this.persons = persons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(getPersons(), that.getPersons());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPersons());
    }
}
