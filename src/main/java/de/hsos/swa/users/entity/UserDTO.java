package de.hsos.swa.users.entity;

/**
 * Data Transfer Object (DTO) für den Benutzer.
 * Diese Klasse wird verwendet, um Benutzerdaten zu übertragen, insbesondere bei der Kommunikation
 * zwischen verschiedenen Schichten der Anwendung, ohne die vollständige Entität `User` zu verwenden.
 * Sie enthält die grundlegenden Informationen eines Benutzers: Name, Passwort und E-Mail-Adresse.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public class UserDTO {
    private String name;
    private String password;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
