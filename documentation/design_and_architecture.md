# Design og Arkitektur
Formålet for denne sektion er at give eventuelle udviklere et overblik over systemets logiske komponenter samt aktørernes relation hertil. Overblikket formidles gennem en række diagrammer; et abstrakt "boksdiagram", der illustrerer den overordnede arkitektur, et par sekvensdiagrammer, som viser de centrale interaktioner, og til sidst et diagram, der visualiserer den anvendte datamodel.

## Arkitektur
Systemet består af en række services, som fremgår af [sekvensdiagrammet](./src/main/resources/images/sequence_diagram_management.svg). I dette projekt er det særligt komponenterne placeret på venstre side af den stiplede linje, der er relevante. Komponenten for klausuleret tilskud har til formål at indkapsle den logik, der håndterer validering af lægemiddelordinationer i forhold til eventuelle klausuler. Stamdata-databasen er markeret med en stiplet linje for at illustrere, at der er tale om en kopi, som opdateres periodisk.

![Boksdiagram: Arkitektur](./src/main/resources/images/architecture.svg)

### Management
Som navnet management antyder, er dette moduls formål at understøtte håndteringen af klausuloprettelse. Gennem brugergrænsefladen kan Lægemiddelstyrelsen (LMS) oprette og administrere klausuler, der efterfølgende indgår i valideringen af kommende ordinationer. Denne proces er illustreret i diagrammet nedenfor.

![Sekvens digram: Management](./src/main/resources/images/sequence_diagram_management.svg)

### Validation
Valideringsmodulet fungerer som grænseflade til FMK og benytter i den forbindelse de klausuler, der er oprettet via management-modulet. I diagrammet er ITUKT-API og ITUKT-cache indrammet i en fælles boks for at illustrere, at de udgør én samlet service. Formålet er, at den nødvendige data til validering indhentes ved komponentens opstart, hvorefter efterfølgende valideringer udelukkende foretages mod den interne cache. Dette er i sekvensdiagrammet illustreret ved de indledende kald til databaserne.

![Sekvens digram: Validation](./src/main/resources/images/sequence_diagram_validation.svg)

## Tech Stack
Systemet er designet med fokus på modularitet, testbarhed og skalérbarhed. Her anvendes en **lagdelt arkitektur**, hvor forretningslogik, datatilgang og præsentationslag er klart adskilt. Dette sikrer, at ændringer i ét lag har minimal indvirkning på andre dele af systemet og fremmer genanvendelighed af komponenter. De centrale teknologier og designvalg omfatter:
- **Java 21 med Spring Boot**: Primært backend-framework med dependency injection og hurtig opsætning.
- **Contract-First API Design**: API’er defineres først via OpenAPI-specifikationer, hvilket sikrer konsistente kontrakter mellem services.
- **RESTful Services**: Tjenester eksponeres som REST API’er, hvilket giver interoperabilitet og standardiseret kommunikation.
- **MariaDB**: Relationel SQL database valgt for at afspejle stamdatabasen.
- **OpenAPI Generator**: Automatiserer generering af klienter, serverstubs og dokumentation baseret på OpenAPI-specifikationer.
- **JUnit 5 (Jupiter)**: Bruges til unit- og integrationstest for høj kodekvalitet og testdrevet udvikling.
- **Testcontainers**: Muliggøre et testmiljø hvori integrationen mellem komponenten og dens afhængigheder, f.eks. databaser eller eksterne services, kan testes.

## Designprincipper
- **Separation of Concerns (SoC)**: Klar adskillelse af ansvar mellem lag for at reducere kompleksitet.
- **Skalerbarhed og fleksibilitet**: Arkitekturen understøtter horisontal skalering i den forstand at modulerne ville kunne adskilles og replikeres.
- **Testbarhed**: Contract-first, JUnit og MockServer sikrer, at både komponenter og integrationer kan testes isoleret.
- **Dokumentation og standardisering**: OpenAPI-specifikationer sikrer, at API-dokumentation altid er opdateret.
- **SOLID-principper**: Arkitekturen følger så hvidt muligt objektorienterede designprincipper for at øge genanvendelse og den overordnede kvalitet af koden:

## Datamodel
I stedet for at gemme rå DSL’er som tekststrenge er det valgt at modellere udtrykkene relationelt, som vist i den nedenstående [Datamodel](./src/main/resources/images/data_model.svg). Denne tilgang muliggør validering, type-sikkerhed og en tættere kobling til den anvendte domænemodel. 

Datamodellen er bygget op omkring klausuler (clauses), der udgør kernen i projektet. Hver klausul indeholder et rekursivt udtryk (expression), som kan tage to former: et binært udtryk (binary_expression) eller en betingelse (string_condition_expression, number_condition_expression, existing_drug_medication_condition_expression).

![ER-Diagram](./src/main/resources/images/data_model.svg)
