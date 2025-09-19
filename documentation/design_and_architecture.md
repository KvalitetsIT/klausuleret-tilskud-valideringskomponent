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

## Datamodel
I stedet for at gemme rå DSL’er som tekststrenge er det valgt at modellere udtrykkene relationelt, som vist i den nedenstående [Datamodel](./src/main/resources/images/data_model.svg). Denne tilgang muliggør validering, type-sikkerhed og en tættere kobling til den anvendte domænemodel. 

Datamodellen er bygget op omkring klausuler (clauses), der udgør kernen i projektet. Hver klausul indeholder et rekursivt udtryk (expression), som kan antage to former: et binært udtryk (binary_expression) eller en betingelse (string_condition_expression, number_condition_expression, existing_drug_medication_condition_expression).

![ER-Diagram](./src/main/resources/images/data_model.svg)
