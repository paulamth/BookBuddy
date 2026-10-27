package de.hsos.swa.users.entity;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.persistence.*;

import java.util.List;

/**
 * Repräsentiert einen Benutzer im System.
 * Diese Entität speichert alle relevanten Informationen zu einem Benutzer, einschließlich seines Namens,
 * seiner E-Mail-Adresse, seines Passworts, seiner Rolle und der Bücher, die er besitzt.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@Entity
@Table(name = "users")
@UserDefinition
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Username
    private String name;
    private String email;
    @Password
    private String password;
    // vielleicht so machen, dass man sich auch ohne E-Mail einloggen kann und dann aber
    // keinen Zugriff auf Tausch hat, sondern nur seine eigenen Bücher im Profil speichern
    // kann, aber nicht zum Tausch anbieten und auch nicht von andern zum Tausch angebotene
    // sehen, dafür die Role:
    @Roles
    public String role;

    @ElementCollection
    private List<Long> besitzendeBuecher;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getBesitzendeBuecher() {
        return besitzendeBuecher;
    }

    public void setBesitzendeBuecher(List<Long> besitzendeBuecher) {
        this.besitzendeBuecher = besitzendeBuecher;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        //this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean verifyPassword(String password) {
        System.out.println("Datenbank: "+this.password);
        System.out.println("Anmeldung: "+password);

        return BCrypt.checkpw(password, this.password);
        //return true;
    }

    public void setRole(String role) {
            this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

