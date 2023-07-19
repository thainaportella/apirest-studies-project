package br.com.tpprojects.personrequestapirest.vo.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;
import jakarta.persistence.Column;
import org.springframework.hateoas.RepresentationModel;
import java.io.Serializable;
import java.util.Objects;

@JsonPropertyOrder({"id", "first_name", "last_name", "address", "gender"}) //coloca uma ordem na requisição
public class PersonVO extends RepresentationModel<PersonVO> implements Serializable {
    private static final long serialVersionUID = 1L;

    

    @JsonProperty("id")
    @Mapping("id")
    private Long key;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;
    private Boolean enabled;

    public PersonVO() {}

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public Long getKey() { return key; }
    public void setKey(Long key) { this.key = key; }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getGender() { return gender; }
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonVO personVO = (PersonVO) o;
        return Objects.equals(getKey(), personVO.getKey()) && Objects.equals(getFirstName(), personVO.getFirstName()) && Objects.equals(getLastName(), personVO.getLastName()) && Objects.equals(getAddress(), personVO.getAddress()) && Objects.equals(getGender(), personVO.getGender()) && Objects.equals(getEnabled(), personVO.getEnabled());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getKey(), getFirstName(), getLastName(), getAddress(), getGender(), getEnabled());
    }
}
