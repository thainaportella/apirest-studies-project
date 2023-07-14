package br.com.tpprojects.personrequestapirest.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_name", unique = true) //não pode ter mais de uma conta com o mesmo username
    private String userName;
    @Column(name="full_name")
    private String fullName;
    @Column(name="password")
    private String password;
    @Column(name="account_non_expired")
    private boolean accountNonExpired;
    @Column(name="account_non_locked")
    private boolean accountNonLocked;
    @Column(name="credentials_non_expired")
    private boolean credentialsNonExpired;
    @Column(name="enabled")
    private boolean enabled;


    // A tabela user_permission poderia ser feita numa classe separada, mas
    // como os atributos dela são poucos, é uma tabela simples e esses dois atributos
    // já foram definidos pela tabela Permission (id permission) e Users (id user), então
    // pode ser implementado na classe mais forte, nesse caso, a users
    @ManyToMany(fetch = FetchType.EAGER) //hibernate - pesquisar o q é mais interessante entre o eager e o lazy
    @JoinTable(name= "user_permission", joinColumns = {@JoinColumn(name = "id_user")},
            inverseJoinColumns = {@JoinColumn(name = "id_permission")})
    private List<Permission> permissions;

    public User() {
    } // JPA precisa de construtor padrão

    public List<String> getRoles() {
        //converter permissions pra roles
        List<String> roles = new ArrayList<>();
        for(Permission permission : permissions) {
            roles.add(permission.getDescription());
            // professor disse que é uma quebra de convenção,
            // mas teve que implementar por causa da UserDetails
        }
        return roles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }
    @Override
    public String getPassword() {
        return this.password;
    }
    @Override
    public String getUsername() {
        return this.userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isAccountNonExpired() == user.isAccountNonExpired() && isAccountNonLocked() == user.isAccountNonLocked() && isCredentialsNonExpired() == user.isCredentialsNonExpired() && isEnabled() == user.isEnabled() && Objects.equals(getId(), user.getId()) && Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getFullName(), user.getFullName()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getPermissions(), user.getPermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserName(), getFullName(), getPassword(), isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled(), getPermissions());
    }
}
