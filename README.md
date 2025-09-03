# klausuleret-tilskud-valideringskomponent

![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-valideringskomponent/workflows/CICD/badge.svg)

**Komponent:** Klausuleret Tilskud – Valideringskomponent  
**Version:** 0.0.1  
**Dato:** 20. august 2025  
**Udarbejdet af:** KvalitetsIT

---

## Oversigt 
- [Udvikler-vejledning](./documentation/developer_guide.md)
- [Design og arkitektur](./documentation/design_and_architecture.md)
- [Konfiguration](./documentation/configuration.md)

## 1. Formål

Denne README er udarbejdet i forbindelse med første leverance af komponenten *Klausuleret Tilskud –
Valideringskomponent*.  
Formålet er at give et klart og præcist overblik over integration, konfiguration og opstart af komponenten, således at
udviklere og driftsteams hurtigt kan etablere og afprøve løsningen i relevante miljøer. Yderligere detaljer findes i oversigten ovenfor.

---

## 2. Endpoints

### 2.1 Komponent

Tjenesten lytter efter forbindelser på port 8080.

Spring Boot Actuator lytter efter forbindelser på port 8081 (dette bruges som Prometheus scrape-endpoint og til
sundhedsovervågning).

### 2.2 Api specifikation

En OpenAPI/Swagger-UI kan findes på nedenstående
[adresse](http://localhost:80/openapi), hvor yderligere beskrivelser af felter, endpoints osv. kan ses:

```
http://localhost:80/openapi
```
