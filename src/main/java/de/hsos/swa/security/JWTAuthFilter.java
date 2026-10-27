package de.hsos.swa.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Set;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Ein Filter für die Authentifizierung von JWT-Tokens.
 * Dieser Filter extrahiert und validiert JWT-Token aus den Cookies einer Anfrage
 * und setzt den entsprechenden Sicherheitskontext für authentifizierte Benutzer.
 *
 * Der Filter überspringt bestimmte Endpunkte wie "login", "guest-login", "users" und "public".
 * Für alle anderen Endpunkte wird das Token überprüft und die Benutzerrolle gesetzt.
 *
 * Quelle: https://stackoverflow.com/questions/29766673/custom-jax-rs-authorization-using-jwt-in-each-request
 * https://antoniogoncalves.org/2016/10/03/securing-jax-rs-endpoints-with-jwt/
 * @author Paula Matthies
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    @Inject
    JWTParser parser;

    /**
     * Filtert eingehende Anfragen und überprüft das JWT-Token auf Authentifizierung.
     * Wenn ein gültiges JWT-Token gefunden wird, wird der Sicherheitskontext gesetzt,
     * andernfalls wird eine UNAUTHORIZED-Antwort zurückgegeben.
     *
     * @param requestContext Der Kontext der eingehenden Anfrage.
     * @throws IOException Wenn ein Fehler beim Verarbeiten der Anfrage auftritt.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("login") || path.startsWith("guest-login") || path.startsWith("users") ||  path.startsWith("public")) {
            return;
        }
        String token = null;
        if (requestContext.getCookies().containsKey("Authorization")) {
            token = requestContext.getCookies().get("Authorization").getValue();
            System.out.println("JWTAuthFilter: Empfangenes Token: " + token);
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                JsonWebToken jwt = parser.parse(token);

                final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> jwt.getClaim("sub");
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        Set<String> roles = jwt.getGroups();
                        return roles != null && roles.contains(role);
                    }

                    @Override
                    public boolean isSecure() {
                        return currentSecurityContext.isSecure();
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return "Bearer";
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Ungültiges Token: " + e.getMessage()).build());
            }
        }
    }
}
