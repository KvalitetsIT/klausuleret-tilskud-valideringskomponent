# Installationsvejledning
**Komponent:** Klausuleret Tilskud – Valideringskomponent  
**Version:** 1.0  
**Dato:** 14. august 2025  
**Udarbejdet af:** Jonas Holsting @ KvalitetsIT

---

## **1. Formål**
Denne Installationsvejledning er udarbejdet i forbindelse med første leverance af komponenten *Klausuleret Tilskud – Valideringskomponent*.  
Formålet er at give et klart og præcist overblik over integration, konfiguration og opstart af komponenten, således at udviklere og driftsteams hurtigt kan etablere og afprøve løsningen i relevante miljøer.

## Quick Start
Følg nedenstående trin for at installere og starte *Klausuleret Tilskud – Valideringskomponent*

### Trin 1 – Klargør miljøet
1. Sørg for, at **Docker** og **Docker Compose** er installeret og fungerer.
    - [Docker Installationsvejledning](https://docs.docker.com/get-docker/)
    - [Docker Compose Installationsvejledning](https://docs.docker.com/compose/install/)
2. Kontrollér, at du har adgang til nødvendige Docker-images:
    - [trifork/tilskud-sdm-db:latest](https://hub.docker.com/layers/trifork/tilskud-sdm-db)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent:dev](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:dev](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation)

### Trin 2 – Justér nedenstående docker-compose og opsæt miljøvariabler
Opret en `docker-compose.yml` eller indsæt de korrekte værdier for miljøvariablerne (se **Konfiguration** i afsnit 5) i nedenstående eksempel Sørg for, at JDBC-brugeroplysningerne er korrekte, og at de matcher databaseinstanserne.

#### Docker Compose
Hermed et eksempel på en *Docker Compose*-opsætning, som kan anvendes til at starte komponenten med tilhørende databaseinstanser.

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
    image: kvalitetsit/klausuleret-tilskud-valideringskomponent:latest
    environment:
      - APP_MANAGEMENT_JDBC_URL=jdbc:mariadb://app-db:3306/validation_db
      - APP_MANAGEMENT_JDBC_USERNAME=user
      - APP_MANAGEMENT_JDBC_PASSWORD=secret1234
      - APP_VALIDATION_JDBC_URL=jdbc:mariadb://stamdata-db:3306/sdm_krs_a
      - APP_VALIDATION_JDBC_USERNAME=root
      - APP_VALIDATION_JDBC_PASSWORD=
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
    image: kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:latest
    environment:
      - BASE_URL=/openapi
      - 'SERVER_URLS=[{"url": "http://localhost:8080", "name": "validation"}]'
    ports:
      - 80:8080
```


### Trin 3 – Start applikationen
1. Naviger til mappen med `docker-compose.yml`.
2. Kør følgende kommando:
   ```bash
   docker-compose up -d
   ```

### Trin 4 – Verificér korrekt respons fra komponenten

Nedenstående anmodning omfatter det forventede request from FMK og kan i øvrigt anvendes med henblik på at verificere at komponenten returnerer hvad der forventes.

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

## Api specifikation 
En OpenAPI/swagger-UI kan findes på nedenstående hvori yderligere beskrivelser af felter, endpoints, etc. kan findes.
```
http://localhost:80/openapi
```
### Konfiguration

Den følgende tabel indeholder miljø-variable og dertilhørende beskrivelser.

| Environment variable                         | Description                                                                                                                  | Required |
|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|----------|
| LOG_LEVEL                                    | Log Level for applikation log. Defaults to INFO.                                                                             | No       |
| LOG_LEVEL_FRAMEWORK                          | Log level for framework. Defaults to INFO.                                                                                   | No       |
| CORRELATION_ID                               | HTTP header to take correlation id from. Used to correlate log messages. Defaults to "x-request-id".                         | No       |
| APP_ALLOWED_ORIGINS                          | A list of urls/origins which is to be allowed by CORS.                                                                       | No       |
| APP_MANAGEMENT_JDBC_URL                      | JDBC connection URL                                                                                                          | Yes      |
| APP_MANAGEMENT_JDBC_USERNAME                 | JDBC user                                                                                                                    | Yes      |
| APP_MANAGEMENT_JDBC_PASSWORD                 | JDBC password                                                                                                                | Yes      |
| APP_MANAGEMENT_JDBC_CONNECTION_TEST_QUERY    | Query for testing the JDBC connection. Defaults to using the JDBC driver for validating connections.                         | No       |
| APP_MANAGEMENT_JDBC_CONNECTION_MAX_AGE       | Maximum amount of time (ISO 8601 Duration) a connection is allowed to be in the JDBC connection pool. Defaults to 30 minutes | No       |
| APP_MANAGEMENT_JDBC_CONNECTION_MAX_IDLE_TIME | Maximum amount of time (ISO 8601 Duration) a connection is allowed to sit idle in the JDBC connection pool                   | No       |
| APP_VALIDATION_JDBC_URL                      | JDBC connection URL                                                                                                          | Yes      |
| APP_VALIDATION_JDBC_USERNAME                 | JDBC user                                                                                                                    | Yes      |
| APP_VALIDATION_JDBC_PASSWORD                 | JDBC password                                                                                                                | Yes      |
| APP_VALIDATION_JDBC_CONNECTION_TEST_QUERY    | Query for testing the JDBC connection. Defaults to using the JDBC driver for validating connections.                         | No       |
| APP_VALIDATION_JDBC_CONNECTION_MAX_AGE       | Maximum amount of time (ISO 8601 Duration) a connection is allowed to be in the JDBC connection pool. Defaults to 30 minutes | No       |
| APP_VALIDATION_JDBC_CONNECTION_MAX_IDLE_TIME | Maximum amount of time (ISO 8601 Duration) a connection is allowed to sit idle in the JDBC connection pool                   | No       |
