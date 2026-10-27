package de.hsos.swa.users.boundary;

import de.hsos.swa.users.entity.User;
import de.hsos.swa.users.entity.UserDTO;
import de.hsos.swa.users.service.UserVerwaltung;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.HashSet;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/users")
@SecurityRequirement(name = "Keycloak")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@OpenAPIDefinition(
        info = @Info(
                title = "User-Management-API",
                version = "1.0",
                description = "API zur Registrierung, Anmeldung und Verwaltung von Benutzern"
        ),
        security = @SecurityRequirement(name = "Keycloak")
)
/**
 * Diese Ressource stellt Endpunkte zur Verwaltung von Benutzerkonten bereit.
 * Sie umfasst die Registrierung, Anmeldung, Gast-Login und das Löschen von Benutzern.
 * Die Authentifizierung erfolgt über JWT-Tokens in Kombination mit Keycloak.
 *
 * @author Paula Matthies
 *
 */
public class UserResource {

    @Inject
    private UserVerwaltung userService;

    @Inject
    Template register;

    @Inject
    Template login;

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    @Operation(
            summary = "Zeigt das Registrierungsformular",
            description = "Zeigt das Formular zur Benutzerregistrierung an."
    )
    public Response showRegistrationForm() {
        TemplateInstance templateInstance = register.data("error", "");
        return Response.ok(templateInstance.render()).build();
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    @Operation(
            summary = "Zeigt das Login-Formular",
            description = "Zeigt das Formular für die Benutzeranmeldung an."
    )
    public Response showLoginForm() {
        TemplateInstance templateInstance = login.data("error", "");
        return Response.ok(templateInstance.render()).build();
    }

    @POST
    @Path("/register")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(
            summary = "Registriert einen neuen Benutzer",
            description = "Registriert einen neuen Benutzer mit den angegebenen Daten."
    )
    @APIResponses({
            @APIResponse(responseCode = "303", description = "Benutzer erfolgreich registriert, Weiterleitung zum Login"),
            @APIResponse(responseCode = "400", description = "Benutzer mit dieser E-Mail existiert bereits")
    })
    @Retry(maxRetries = 3)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4)
    public Response registerUser(@FormParam("name") String name,
                                 @FormParam("email") String email,
                                 @FormParam("password") String password) {

        User user = userService.getUserByEmail(email);
        if (user == null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setName(name);
            userDTO.setEmail(email);
            userDTO.setPassword(password);
            userService.createUser(userDTO);

            return Response.seeOther(URI.create("/users/login")).build();
        } else {
            return Response.ok(register.data("error", "User mit dieser Email existiert bereits!").render()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Meldet einen Benutzer an",
            description = "Authentifiziert einen Benutzer mit den angegebenen Anmeldedaten."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Erfolgreiche Anmeldung, Token generiert"),
            @APIResponse(responseCode = "401", description = "Ungültige Anmeldedaten")
    })
    @Retry(maxRetries = 3)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4)
    public Response loginUser(@FormParam("email") String email,
                              @FormParam("password") String password) {
        User user = userService.getUserByEmail(email);

        if (user != null && user.verifyPassword(password)) {
            return createToken(user.getId().toString(), user.getEmail(), user.getRole());
        } else {
            return Response.ok(login.data("error", "Ungültige Anmeldedaten!").render()).build();
        }
    }

    @GET
    @Path("/guest-login")
    @Operation(
            summary = "Login als Gast",
            description = "Erstellt ein Gast-Token für den anonymen Zugriff."
    )
    @APIResponse(responseCode = "200", description = "Gast-Login erfolgreich")
    public Response loginAsGuest() {
        System.out.println("guest-login");
        return createToken("guest", "guest@bookbuddy.com", "GAST");
    }

    private Response createToken(String subject, String email, String role) {
        Set<String> roles = new HashSet<>();
        roles.add(role);

        String token = Jwt.issuer("bookbuddy")
                .subject(subject)
                .upn(email)
                .groups(roles)
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();

        NewCookie jwtCookie = new NewCookie("Authorization", "Bearer " + token, "/", null, "JWT Token", 3600, false, true);

        if ("GAST".equals(role)) {
            return Response.seeOther(URI.create("/books/search"))
                    .cookie(jwtCookie)
                    .build();
        } else {
            return Response.seeOther(URI.create("/books/list"))
                    .cookie(jwtCookie)
                    .build();
        }
    }


    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(
            summary = "Löscht einen Benutzer",
            description = "Löscht einen Benutzer basierend auf der Benutzer-ID."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Benutzer erfolgreich gelöscht"),
            @APIResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @Retry(maxRetries = 3)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4)
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}