# klausuleret-tilskud-valideringskomponent

![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-valideringskomponent/workflows/CICD/badge.svg)

**Komponent:** Klausuleret Tilskud – Valideringskomponent  
**Version:** 0.0.1  
**Dato:** 20. august 2025  
**Udarbejdet af:** KvalitetsIT

---

## Oversigt 
1. [Installationsvejledning](./documentation/installationguide.md)
2. [Design og arkitektur](./documentation/design_and_architecture.md)
3. [Konfiguration](./documentation/configuration.md)

## 1. Formål

Denne README er udarbejdet i forbindelse med første leverance af komponenten *Klausuleret Tilskud –
Valideringskomponent*.  
Formålet er at give et klart og præcist overblik over integration, konfiguration og opstart af komponenten, således at
udviklere og driftsteams hurtigt kan etablere og afprøve løsningen i relevante miljøer. Yderligere detaljer findes i oversigten ovenfor.

---

### 2. Building and testing

To run test with working debugging:\
`mvn clean install`

To run (same) tests against docker-compose:\
`mvn clean install -Pdocker-test`

Debugging vil i dette tilfælde ikke virke fordi tjenesten kører i en container som bygges on-demand af test-kørslen.

---

## 3. Endpoints

### 3.1 Komponent

Tjenesten lytter efter forbindelser på port 8080.

Spring Boot Actuator lytter efter forbindelser på port 8081 (dette bruges som Prometheus scrape-endpoint og til
sundhedsovervågning).

### 3.2 Api specifikation

En OpenAPI/Swagger-UI kan findes på nedenstående
[adresse](http://localhost:80/openapi), hvor yderligere beskrivelser af felter, endpoints osv. kan ses:

```
http://localhost:80/openapi
```

---

## 4. Opdatering af afhængigheder
Som standard bruger vi GitHub Actions som vores CI/CD-platform, og den kan også håndtere opdateringer af afhængigheder. Vi benytter GitHubs [Dependabot](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuring-dependabot-version-updates)
til at oprette pull requests med opdateringer af afhængigheder.