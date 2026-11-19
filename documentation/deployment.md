# Idriftsættelse

Komponenten findes som image på Dockerhub: [kvalitetsit/klausuleret-tilskud-valideringskomponent](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent).

Den sættes op med en tilhørende mariadb database, samt en forbindelse til SDM databasen.

Konfigurationen er beskrevet her: [Konfiguration](configuration.md)

Herefter vil komponenten være tilgængelig på port 8080, med et health endpoint på `/actuator/health`


### Swagger UI
Et image med tilhørende Swagger UI findes på Dockerhub: [kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation).

Her ses yderligere beskrivelser af felter, endpoints osv.