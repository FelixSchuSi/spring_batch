# spring_batch

```Repository zur Vorstellung von Felix Schulze Sindern am 26.01.2020```
## Vorbereitung
1. Repository klonen

- Mit https: ```git clone https://git.fh-muenster.de/winfo/software-engineering/eaa-2020/spring_batch.git```

- Mit ssh: ```git clone ssh://git@git.fh-muenster.de:2323/winfo/software-engineering/eaa-2020/spring_batch.git```

### Transaction Microservice starten
> docker-compose up

Die Funktion des Microservices kann durch den Aufruf von ```http://localhost:3443/transactions?accountId=1``` mit einem Browser oder Postman überprüft werden.
Die Antwort sollte wie folgt aussehen:
> [{"transactionId":58,"accountId":1,"description":"Tagchat","credit":null,&nbsp;
"debit":-1612,"timestamp":1611286955000,"transactionAmount":-1612,"id":58}]

### Probelauf des Statement Creators
> cd statement-creator/<br>
  mvn spring-boot:run

Der Statement Creator hat im Ordner ```/statement-creator/target``` (noch unvollständige) Kontoauszüge erstellt.
Die Datei ```/statement-creator/target/kontoauszug-1.txt``` sollte folgenden Inhalt haben:
```
                                                                                                   Kundenservice Hotline
                                                                                                            (0800) 12345
                                                                                      Rund um die Uhr für Sie erreichbar



Gibbie Peiro                                                                                                Münster Bank
131 Killdeer Way                                                                                        Corrensstraße 25
Hamilton, Ohio 28815                                                                                       48149 Münster

Kontoauszug für Ihr Konto mit der IBAN: DE70500105177613517489
                                                                 Kontostand am 2020-12-27:    194,52 €
               Es wurden keine Transaktionen im Zeitraum getätigt.
                                                                 Kontostand am 2020-12-30:    194,52 €
```