# Udvikler-vejledning 
---
## 1. Byg og test

Eksekvering af test med fungerende debugging:\
`mvn clean install`

Eksekvering af selvsamme test-sæt imod docker-compose:\
`mvn clean install -Pdocker-test`

Debugging vil i dette tilfælde ikke virke fordi tjenesten kører i en container som bygges on-demand af test-kørslen.

---
## 2. Kørsel af komponenten 

Følg nedenstående trin for at starte *Klausuleret Tilskud – Valideringskomponent* og dertilhørende afhængigheder

---
### Trin 1 – Klargør miljøet

1. Sørg for, at **Docker** og **Docker Compose** er installeret og fungerer.
    - [Docker Installationsvejledning](https://docs.docker.com/get-docker/)
    - [Docker Compose Installationsvejledning](https://docs.docker.com/compose/install/)

2. Kontrollér, at du har adgang til nødvendige Docker-images:
    - [trifork/tilskud-sdm-db:latest](https://hub.docker.com/r/trifork/tilskud-sdm-db)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent:0.0.1](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent)
    - [kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:0.0.1](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation)

---
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

---
### Trin 3 – Start applikationen

1. Naviger til mappen med `docker-compose.yml`.
2. Kør følgende kommando: `docker-compose up -d`

---
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

### 3. Opdatering af afhængigheder
Som standard bruger vi GitHub Actions som vores CI/CD-platform, og den kan også håndtere opdateringer af afhængigheder. Vi benytter GitHubs [Dependabot](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuring-dependabot-version-updates)
til at oprette pull requests med opdateringer af afhængigheder.

---
