TRUNCATE TABLE trades RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE books RESTART IDENTITY CASCADE;
TRUNCATE TABLE User_besitzendeBuecher RESTART IDENTITY CASCADE;

INSERT INTO users (id, name, email, password, role)
VALUES
    (1, 'Alice', 'alice@example.com', '$2a$10$Vbc8Y4DDa2AEXum5iUr3Fea5QyXPZSUTgGlHOQyTzRlSrgcGbD73i', 'USER'),
    (2, 'Rieke', 'rieke@example.com', '$2a$10$bL5/pHxGb1LToIGaSUG3/ewvzWg1lwbUbuRa0fjsbKjzYLhgrE5EO', 'USER'),
    (3, 'Paula', 'paula@example.com', '$2a$10$BrKXb8ecEJ8FuV5vQL1QI.s6ZoHOQR1qSDcOQS8ZrxguZVVsic3SO', 'USER');


INSERT INTO books (databaseId, idInExternApi)
VALUES
    (1, '0jzuEAAAQBAJ'), -- SAPIENS - Eine kurze Geschichte der Menschheit
    (2, 'IIjpDQAAQBAJ'), -- Homo Deus
    (3, 'V03mEAAAQBAJ'), -- Romeo und Julia (Zweisprachige Ausgabe: Deutsch-Englisch)
    (4, 'vs4mBQAAQBAJ'), -- Kafka am Strand
    (5, 'L_4rEQAAQBAJ'), -- Der inoffizielle Adventskalender für alle Fans von The Witcher
    (6, 'dJ-kBwAAQBAJ'), -- Die Tribute von Panem 3. Flammender Zorn
    (7, '16F3DwAAQBAJ'), -- The Witcher, Band 1 - Im Glashaus
    (8, 'P1vjPQAACAAJ'), -- Geheime Botschaften
    (9, '6ERnEAAAQBAJ'), -- Die dunklen Fälle des Harry Dresden - Silberlinge
    (10, 'U1AZmgEACAAJ'), -- Iphigenia in Tauris
    (11, 'dEXrDwAAQBAJ'); -- Die Genom-E-volution


INSERT INTO User_besitzendeBuecher (User_id, besitzendeBuecher)
VALUES
    (1, 1), -- Alice besitzt Buch 1
    (1, 2), -- Alice besitzt Buch 2
    (1, 8), -- Alice besitzt Buch 8
    (1, 10), -- Alice besitzt Buch 10
    (2, 3), -- Rieke besitzt Buch 3
    (2, 4), -- Rieke besitzt Buch 4
    (2, 7), -- Rieke besitzt Buch 7
    (2, 11), -- Rieke besitzt Buch 11
    (3, 5), -- Paula besitzt Buch 5
    (3, 6), -- Paula besitzt Buch 6
    (3, 9); -- Paula besitzt Buch 9

INSERT INTO trades (id, initiatorUserId, acceptorUserId, databaseBookIdFirstBook, databaseBookIdSecondBook, bewertung, status)
VALUES
    (1, 1, 0, 10, 0, '', 0),  -- Alice startet Tausch mit Buch 10, keine Reaktion
    (2, 3, 1, 5, 2, '', 1),  -- Paula startet Tausch mit Buch 5, Alice schlägt Buch 2 vor
    (4, 2, 0, 4, 0, '', 0),  -- Rieke startet Tausch mit Buch 4, keine Reaktion bis jetzt
    (5, 2, 1, 3, 1, '', 2),  -- abgeschlossener Tausch zwischen Rieke und Alice mit Buch 3 und 1
    (6, 2, 3, 11, 6, 'wurde schnell vorbeigebracht', 2), -- abgeschlossener Tausch zwischen Rieke und Paula mit Buch 11 und 6
    (7, 2, 1, 7, 8, '', 1)  -- Rieke startet Tausch mit Buch 7, Alice schlägt Buch 8 vor
;
-- folgendes da wir hier ja IDs manuell vergeben haben und Postgres
-- bei zukünftigem Einfügen weiß, welches die nächste freie ID ist
SELECT setval('books_databaseid_seq', (SELECT MAX(databaseId) FROM books));
SELECT setval('trades_id_seq', (SELECT MAX(id) FROM trades));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
