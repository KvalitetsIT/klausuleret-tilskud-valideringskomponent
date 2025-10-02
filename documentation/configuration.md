# Konfiguration

Den følgende tabel indeholder miljø-variable og dertilhørende beskrivelser.

| Environment variable                                        | Description                                                                                                                       | Required |
|-------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|----------|
| LOG_LEVEL                                                   | Logniveau for applikations-log. Standardværdi er INFO.                                                                            | Nej      |
| LOG_LEVEL_FRAMEWORK                                         | Logniveau for framework. Standardværdi er INFO.                                                                                   | Nej      |
| CORRELATION_ID                                              | HTTP-header, der angiver hvilken correlation id der skal bruges. Bruges til at korrelere logbeskeder. Standard er "x-request-id". | Nej      |
| ITUKT_COMMON_ALLOWEDORIGINS                                 | En liste af URL’er/origins som skal tillades af CORS.                                                                             | Nej      |
| ITUKT_MANAGEMENT_CACHE_CRON                                 | Specifiere hvor ofte mellemlageret/cachen for klausuler opdateres i form af et CRON udtryk (0 0 0 * * *)                          | Ja       |
| ITUKT_COMMON_ITUKTDB_URL                                    | JDBC-forbindelses-URL til itukt-databasen                                                                                         | Ja       |
| ITUKT_COMMON_ITUKTDB_USERNAME                               | JDBC-brugernavn                                                                                                                   | Ja       |
| ITUKT_COMMON_ITUKTDB_PASSWORD                               | JDBC-adgangskode                                                                                                                  | Ja       |
| ITUKT_COMMON_ITUKTDB_CONNECTION_TESTQUERY                   | Forespørgsel til test af JDBC-forbindelsen. Standard er at bruge JDBC-driveren til at validere forbindelser.                      | Nej      |
| ITUKT_COMMON_ITUKTDB_CONNECTION_MAXAGE                      | Maksimal tid (ISO 8601-varighed), en forbindelse må være i JDBC-connection pool’en. Standard er 30 minutter                       | Nej      |
| ITUKT_COMMON_ITUKTDB_CONNECTION_MAXIDLETIME                 | Maksimal tid (ISO 8601-varighed), en forbindelse må være inaktiv i JDBC-connection pool’en                                        | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_URL                    | DBC-forbindelses-URL til stamdata-databasen                                                                                       | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_USERNAME               | JDBC-brugernavn                                                                                                                   | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_PASSWORD               | JDBC-adgangskode                                                                                                                  | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_TESTQUERY   | Forespørgsel til test af JDBC-forbindelsen. Standard er at bruge JDBC-driveren til at validere forbindelser..                     | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_MAXAGE      | Maksimal tid (ISO 8601-varighed), en forbindelse må være i JDBC-connection pool’en. Standard er 30 minutter                       | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_MAXIDLETIME | Maksimal tid (ISO 8601-varighed), en forbindelse må være inaktiv i JDBC-connection pool’en.                                       | Nej      |
| ITUKT_VALIDATION_STAMDATA_CACHE_CRON                        | Specifiere hvor ofte mellemlageret/cachen for stamdata opdateres i form af et CRON udtryk (0 0 0 * * *)                           | Ja       |

Database-forbindelsespoolen er sat op ved hjælp af HikariCP og benytter dens standardindstillinger, som er dokumenteret
på: </br> https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#frequently-used.
