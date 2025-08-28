# klausuleret-tilskud-valideringskomponent

![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-valideringskomponent/workflows/CICD/badge.svg)

**Komponent:** Klausuleret Tilskud – Valideringskomponent  
**Version:** 0.0.1  
**Dato:** 20. august 2025  
**Udarbejdet af:** KvalitetsIT

---

## 1. Formål

Denne README er udarbejdet i forbindelse med første leverance af komponenten *Klausuleret Tilskud –
Valideringskomponent*.  
Formålet er at give et klart og præcist overblik over integration, konfiguration og opstart af komponenten, således at
udviklere og driftsteams hurtigt kan etablere og afprøve løsningen i relevante miljøer.

---

### 2. Building and testing

To run test with working debugging:\
`mvn clean install`

To run (same) tests against docker-compose:\
`mvn clean install -Pdocker-test`

Debugging vil i dette tilfælde ikke virke fordi tjenesten kører i en container som bygges on-demand af test-kørslen.

## 3. Installationsvejledning

Følg nedenstående trin for at installere og starte *Klausuleret Tilskud – Valideringskomponent*

### Trin 1 – Klargør miljøet

1. Sørg for, at **Docker** og **Docker Compose** er installeret og fungerer.
    - [Docker Installationsvejledning](https://docs.docker.com/get-docker/)
    - [Docker Compose Installationsvejledning](https://docs.docker.com/compose/install/)

2. Kontrollér, at du har adgang til nødvendige Docker-images:
    - [trifork/tilskud-sdm-db:latest](https://hub.docker.com/r/trifork/tilskud-sdm-db)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent:0.0.1](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:0.0.1](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation)

### Trin 2 – Justér nedenstående docker-compose og opsæt miljøvariabler

Opret en `docker-compose.yml` eller indsæt de korrekte værdier for miljøvariablerne (se **Konfiguration** i afsnit 6) i
nedenstående eksempel. Sørg for, at JDBC-brugeroplysningerne er korrekte, og at de matcher databaseinstanserne.

#### Docker Compose

Hermed et eksempel på en *Docker Compose*-opsætning, som kan anvendes til at starte komponenten med tilhørende
databaseinstanser.

```
  services:
  app-db:
    image: mariadb:10.11
    environment:
      - MYSQL_ROOT_PASSWORD=rootroot
      - MYSQL_DATABASE=validation_db
      - MYSQL_USER=user
      - MYSQL_PASSWORD=secret1234
    healthcheck:
      test: mysql --user=root --password=rootroot -e 'show databases;'
      interval: 2s
      timeout: 2s
      retries: 10
    ports:
      - 3306:3306
    volumes:
      - ../configuration/ddl:/docker-entrypoint-initdb.d

  stamdata-db:
    image: trifork/tilskud-sdm-db:latest
    ports:
      - 3307:3306
    environment:
      - MYSQL_DATABASE=sdm_krs_a
      - MYSQL_USER=user
      - MYSQL_PASSWORD=secret1234
    healthcheck:
      test: mysql -e 'show databases;'
      interval: 2s
      timeout: 2s
      retries: 10
    
  validation-component:
    image: kvalitetsit/klausuleret-tilskud-valideringskomponent:0.0.1
    environment:
      - ITUKT_COMMON_ITUKTDB_URL=jdbc:mariadb://itukt-db:3306/itukt_db
      - ITUKT_COMMON_ITUKTDB_USERNAME=user
      - ITUKT_COMMON_ITUKTDB_PASSWORD=secret1234
      - ITUKT_VALIDATION_STAMDATA_STAMDATADB_URL=jdbc:mariadb://stamdata-db:3306/sdm_krs_a
      - ITUKT_VALIDATION_STAMDATA_STAMDATADB_USERNAME=root
      - ITUKT_VALIDATION_STAMDATA_STAMDATADB_PASSWORD=
      - JVM_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
    depends_on:
      app-db:
        condition: service_healthy
      stamdata-db:
        condition: service_healthy
    ports:
      - 8080:8080
      - 8081:8081
      - 5005:5005

  documentation-and-test:
    image: kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:0.0.1
    environment:
      - BASE_URL=/openapi
      - 'SERVER_URLS=[{"url": "http://localhost:8080", "name": "validation"}]'
    ports:
      - 80:8080
```

### Trin 3 – Start applikationen

1. Naviger til mappen med `docker-compose.yml`.
2. Kør følgende kommando: `docker-compose up -d`

### Trin 4 – Verificér korrekt respons fra komponenten

Nedenstående anmodning omfatter de forventede oplysninger fra FMK og kan i øvrigt anvendes med henblik på at verificere
at komponenten returnerer hvad der forventes.

Request:

```
curl -X 'POST' \
  'http://localhost:8080/2025/08/01/validate' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "skipValidations": [
    10908
  ],
  "personIdentifier": "1234567890",
  "age": 62,
  "validate": [
    {
      "action": "CreateDrugMedication",
      "elementPath": "CreateDrugMedicationRequest.DrugMedication[2]",
      "newDrugMedication": {
        "createdBy": {
          "specialityCode": "PSYK",
          "organisationSpeciality": "psykiatri"
        },
        "reportedBy": {
          "specialityCode": "PSYK",
          "organisationSpeciality": "psykiatri"
        },
        "createdDateTime": "2025-06-13T14:24:03+02:00",
        "drugIdentifier": "28100902676",
        "indicationCode": "31"
      }
    }
  ],
  "existingDrugMedications": [
    {
      "drugIdentifier": "28100645874",
      "atcCode": "J01CE02",
      "formCode": "TABFILM",
      "routeOfAdministrationCode": "OK"
    }
  ]
}'
```

Respons:

```
{
  "validationStatus": "validationSuccess"
}
```

---

## 5. Endpoints

### 5.1 Komponent

Tjenesten lytter efter forbindelser på port 8080.

Spring Boot Actuator lytter efter forbindelser på port 8081 (dette bruges som Prometheus scrape-endpoint og til
sundhedsovervågning).

### 5.2 Api specifikation

En OpenAPI/Swagger-UI kan findes på nedenstående
adresse, hvor yderligere beskrivelser af felter, endpoints osv. kan ses:

```
http://localhost:80/openapi
```

---

## 6. Konfiguration

Den følgende tabel indeholder miljø-variable og dertilhørende beskrivelser.

| Environment variable                                          | Description                                                                                                                       | Required |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|----------|
| LOG_LEVEL                                                     | Logniveau for applikations-log. Standardværdi er INFO.                                                                            | Nej      |
| LOG_LEVEL_FRAMEWORK                                           | Logniveau for framework. Standardværdi er INFO.                                                                                   | Nej      |
| CORRELATION_ID                                                | HTTP-header, der angiver hvilken correlation id der skal bruges. Bruges til at korrelere logbeskeder. Standard er "x-request-id". | Nej      |
| APP_ALLOWED_ORIGINS                                           | En liste af URL’er/origins som skal tillades af CORS.                                                                             | Nej      |
| ITUKT_COMMON_ITUKTDB_URL                                      | JDBC-forbindelses-URL til itukt-databasen                                                                                         | Ja       |
| ITUKT_COMMON_ITUKTDB_USERNAME                                 | JDBC-brugernavn                                                                                                                   | Ja       |
| ITUKT_COMMON_ITUKTDB_PASSWORD                                 | JDBC-adgangskode                                                                                                                  | Ja       |
| ITUKT_COMMON_ITUKTDB_CONNECTION_TEST_QUERY                    | Forespørgsel til test af JDBC-forbindelsen. Standard er at bruge JDBC-driveren til at validere forbindelser.                      | Nej      |
| ITUKT_COMMON_ITUKTDB_CONNECTION_MAX_AGE                       | Maksimal tid (ISO 8601-varighed), en forbindelse må være i JDBC-connection pool’en. Standard er 30 minutter                       | Nej      |
| ITUKT_COMMON_ITUKTDB_CONNECTION_MAX_IDLE_TIME                 | Maksimal tid (ISO 8601-varighed), en forbindelse må være inaktiv i JDBC-connection pool’en                                        | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_URL                      | DBC-forbindelses-URL til stamdata-databasen                                                                                       | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_USERNAME                 | JDBC-brugernavn                                                                                                                   | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_PASSWORD                 | JDBC-adgangskode                                                                                                                  | Ja       |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_TEST_QUERY    | Forespørgsel til test af JDBC-forbindelsen. Standard er at bruge JDBC-driveren til at validere forbindelser..                     | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_MAX_AGE       | Maksimal tid (ISO 8601-varighed), en forbindelse må være i JDBC-connection pool’en. Standard er 30 minutter                       | Nej      |
| ITUKT_VALIDATION_STAMDATA_STAMDATADB_CONNECTION_MAX_IDLE_TIME | Maksimal tid (ISO 8601-varighed), en forbindelse må være inaktiv i JDBC-connection pool’en.                                       | Nej      |

Database-forbindelsespoolen er sat op ved hjælp af HikariCP og benytter dens standardindstillinger, som er dokumenteret på: </br> https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#frequently-used.

---

## 7. Opdatering af afhængigheder
Som standard bruger vi GitHub Actions som vores CI/CD-platform, og den kan også håndtere opdateringer af afhængigheder. Vi benytter GitHubs [Dependabot](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuring-dependabot-version-updates)
til at oprette pull requests med opdateringer af afhængigheder. 

## 8. Udvikler-guide
Formålet for denne sektion er at give eventuelle udviklere et overblik over systemets logiske komponenter samt aktørernes relation hertil. Overblikket formidles gennem en række diagrammer; et abstrakt "boksdiagram", der illustrerer den overordnede arkitektur, et par sekvensdiagrammer, som viser de centrale interaktioner, og til sidst et ER-diagram, der visualiserer den anvendte datamodel.

### Arkitektur
Systemet består af en række services, som fremgår af [boksdiagrammet](./documentation/src/main/resources/images/sequence_diagram_management.svg). I dette projekt er det særligt komponenterne placeret på venstre side af den stiplede linje, der er relevante. Komponenten for klausuleret tilskud har til formål at indkapsle den logik, der håndterer validering af lægemiddelordinationer i forhold til eventuelle klausuler. Stamdata-databasen er markeret med en stiplet linje for at illustrere, at der er tale om en kopi, som opdateres periodisk.

![Boksdiagram: Arkitektur](./documentation/src/main/resources/images/architecture.svg)

#### Management
Som navnet management antyder, er dette moduls formål at understøtte håndteringen af klausuloprettelse. Gennem brugergrænsefladen kan Lægemiddelstyrelsen (LMS) oprette og administrere klausuler, der efterfølgende indgår i valideringen af kommende ordinationer. Denne proces er illustreret i diagrammet nedenfor.
![Sekvens digram: Management](./documentation/src/main/resources/images/sequence_diagram_management.svg)

#### Validation
Valideringsmodulet fungerer som grænseflade til FMK og benytter i den forbindelse de klausuler, der er oprettet via management-modulet. I diagrammet er ITUKT-API og ITUKT-cache indrammet i en fælles boks for at illustrere, at de udgør én samlet service. Formålet er, at den nødvendige data til validering indhentes ved komponentens opstart, hvorefter efterfølgende valideringer udelukkende foretages mod den interne cache. Dette er i sekvensdiagrammet illustreret ved de indledende kald til databaserne.

![Sekvens digram: Validation](./documentation/src/main/resources/images/sequence_diagram_validation.svg)

### Datamodel
I stedet for at gemme rå DSL’er som tekststrenge er det valgt at modellere udtrykkene relationelt, som vist i det nedenstående [ER-Diagram](./documentation/src/main/resources/images/data_model.svg). Denne tilgang muliggør validering, type-sikkerhed og en tættere kobling til den anvendte domænemodel. 

Datamodellen er bygget op omkring klausuler (clauses), der udgør kernen i projektet. Hver klausul indeholder et rekursivt udtryk (expression), som kan antage tre former: et binært udtryk (binary_expression), et udtryk omsluttet af parenteser (parenthesized_expression) eller en betingelse (condition_expression).

![ER-Diagram](./documentation/src/main/resources/images/data_model.svg)