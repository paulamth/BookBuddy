# BookBuddy

Ein REST-Backend zum Tauschen von Büchern. Registrierte Nutzer:innen legen Bücher in ihrer eigenen Bibliothek ab, suchen über die Google Books API nach Titeln und schlagen einander Tausche vor. Dazu gibt es eine einfache Weboberfläche mit Qute-Templates.

Entstanden als **Teamprojekt zu zweit** (Paula Matthies und Rieke Lübbe) im Wintersemester 2024/25 an der Hochschule Osnabrück.

## Funktionen

- Registrierung und Anmeldung, zusätzlich ein Gastzugang nur für die Buchsuche
- Eigene Bibliothek: Bücher hinzufügen, auflisten und entfernen
- Buchsuche nach Titel, Autor und Erscheinungsjahr über die Google Books API
- Tausch: Tausch anbieten, Gegenbuch vorschlagen, annehmen oder ablehnen, abgeschlossene Tausche bewerten
- Dokumentation der API mit OpenAPI und Swagger UI

## Aufbau

Der Code ist nach fachlichen Bereichen aufgeteilt, jeder Bereich in dieselben Schichten:

```
src/main/java/de/hsos/swa/
├── books/      Bücher und Buchsuche
├── trades/     Tausche
├── users/      Nutzer:innen und Anmeldung
└── security/   JWT-Filter für alle geschützten Anfragen
```

| Schicht | Aufgabe |
|---|---|
| `boundary` | REST-Endpunkte (JAX-RS) und der REST-Client für Google Books |
| `service` | Geschäftslogik, hinter einem Interface pro Bereich |
| `gateway` | Datenbankzugriff über JPA (EntityManager), hinter einem Interface pro Bereich |
| `entity` | JPA-Entitäten und DTOs |
| `books/acl` | Schnittstelle, über die Tausche und Nutzer:innen Buchinformationen abfragen, ohne direkt auf die Buchlogik zuzugreifen |

## Technik

Java 21 · Quarkus 3 · JAX-RS (Quarkus REST) · Hibernate ORM (JPA) · PostgreSQL · SmallRye JWT · BCrypt · SmallRye Fault Tolerance · MicroProfile REST Client · Qute · OpenAPI / Swagger UI · JUnit 5 und REST Assured

## Lokal starten

### Voraussetzungen

- Java 21 (neuere Versionen wie Java 25 werden von der verwendeten Quarkus-Version noch nicht unterstützt)
- Docker (Quarkus startet im Entwicklungsmodus automatisch eine PostgreSQL-Datenbank in einem Container)
- OpenSSL zum Erzeugen der Schlüssel

### 1. JWT-Schlüsselpaar erzeugen

Die Anmelde-Token werden mit einem RSA-Schlüssel signiert. Das Schlüsselpaar ist bewusst nicht im Repository und wird einmalig lokal erzeugt:

```sh
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out src/main/resources/publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out src/main/resources/privateKey.pem
rm rsaPrivateKey.pem
```

Alle `.pem`-Dateien sind in der `.gitignore` eingetragen.

### 2. Google-Books-API-Schlüssel

Bibliothek und Suche laden die Buchdaten live von der Google Books API. Ohne Schlüssel lehnt Google schnell mit „Too Many Requests“ (429) ab, deshalb wird ein eigener, kostenloser Schlüssel benötigt:

1. In der [Google Cloud Console](https://console.cloud.google.com/) ein Projekt anlegen.
2. Unter „APIs & Dienste“ → „Bibliothek“ die **Books API** aktivieren.
3. Unter „APIs & Dienste“ → „Anmeldedaten“ einen **API-Schlüssel** erstellen und ihn auf die Books API beschränken.
4. Den Schlüssel vor dem Start als Umgebungsvariable setzen:

```sh
export BOOK_API_KEY=dein-schlüssel
```

Der Schlüssel gehört nicht ins Repository.

### 3. Starten

```sh
./mvnw quarkus:dev
```

Danach erreichbar unter:

- Anmeldung: http://localhost:8080/users/login
- Buchsuche: http://localhost:8080/books/search
- Swagger UI: http://localhost:8080/swagger-ui

### Testdaten

Beim Start wird die Datenbank mit Beispieldaten aus `src/main/resources/import.sql` gefüllt. Zum Ausprobieren gibt es drei Konten:

| E-Mail | Passwort |
|---|---|
| alice@example.com | password123 |
| rieke@example.com | 222 |
| paula@example.com | 1234 |

Diese Konten gibt es nur in der lokalen Testdatenbank.

## Tests

```sh
./mvnw test
```

Auch für die Tests werden Docker und das JWT-Schlüsselpaar aus Schritt 1 benötigt. Der Test der Buchsuche fragt die echte Google Books API ab und braucht deshalb eine Internetverbindung.

## Für einen Betrieb außerhalb der Entwicklung

Ohne Dev Services werden die Zugangsdaten der Datenbank über Umgebungsvariablen gesetzt: `DB_USERNAME` und `DB_PASSWORD`, die Adresse über `QUARKUS_DATASOURCE_JDBC_URL`.
