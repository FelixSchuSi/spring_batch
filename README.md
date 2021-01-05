# spring_batch

Repository zur Vorstellung *Batchverarbeitung mit Spring Batch* von Felix Schulze Sindern am 26.01.2020
## Vorbereitung
### Transaction Microservice starten
> docker-compose up

Die Funktion des Microservices kann durch den Aufruf von `http://localhost:3443/transactions?accountId=1` mit einem Browser oder Postman überprüft werden.
Die Antwort sollte wie folgt aussehen:
```
[{"transactionId":58,"accountId":1,"description":"Tagchat","credit":null,"debit":-1612,"timestamp":1611286955000,
"transactionAmount":-1612,"id":58}]
```

### Probelauf des Statement Creators
**Hinweis:** Der Transaction Microservice muss während des Probelaufs hochgefahren sein.
> cd statement-creator/<br>
  mvn spring-boot:run

Der Statement Creator hat im Ordner `/statement-creator/target` (noch unvollständige) Kontoauszüge erstellt.
Die Datei `/statement-creator/target/kontoauszug-1.txt` sollte wie folgt aussehen:
```
                                                                                                   Kundenservice Hotline
                                                                                                            (0800) 12345
                                                                                      Rund um die Uhr für Sie erreichbar



Amanda Dudmarsh                                                                                             Münster Bank
127 Tomscot Park                                                                                        Corrensstraße 25
Kansas City, Missouri 64179                                                                                48149 Münster

Kontoauszug für Ihr Konto mit der IBAN: DE95500105174715376938
                                                                 Kontostand am 2020-12-29:  15209,25 €
               Es wurden keine Transaktionen im Zeitraum getätigt.
                                                                 Kontostand am 2021-01-05:      0,00 €

Kontoauszug für Ihr Konto mit der IBAN: DE59500105178486977952
                                                                 Kontostand am 2020-12-29:  18240,75 €
               Es wurden keine Transaktionen im Zeitraum getätigt.
                                                                 Kontostand am 2021-01-05:      0,00 €



```
