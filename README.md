# Progetto Programmazione ad Oggetti (Lorenzo Fratini -- Lorenzo Iacopini)

La seguente applicazione all'avvio effettua il download del dataset (formato .csv) all'indirizzo URL:
https://www.dati.gov.it/api/3/action/package_show?id=866cfedc-8eb9-4784-9ec4-f5730f252e89 <br> a seguito della decodifica del JSON.
Tale dataset verrà salvato nella stessa cartella del progetto in formato .csv con il nome **_ImpreseOOP.csv_**

Il dataset contiene le imprese e gli addetti rilevati nel  9° Censimento dell'Industria e dei Servizi. 
Le unità di rilevazione sono:  le imprese individuali, le società di persone e di capitali, le società cooperative (escluse le cooperative sociali, oggetto della rilevazione sulle istituzioni non profit), i consorzi di diritto privato, gli Enti pubblici economici, le aziende speciali e le aziende pubbliche di servizi. 
Ai fini del censimento è considerata impresa anche il lavoratore autonomo e il libero professionista. 
Le imprese sono classificate per Ateco 2007 (5 digit), descrizione e numero dei lavoratori (dipendenti, indipendenti, esterni, temporanei) 
<br/>Il presente dataset è stato rilasciato dal comune di Milano.

## Funzionamento dell'applicazione

L'applicazione crea un server locale all'indirizzo: <br/>
**http://localhost:8081** <br>

A tale indirizzo è possibile, su richiesta dell'utente, mediante **API REST GET** ottenere diverse informazioni che sono:
1. restituzione dei **metadati** (formato JSON) ovvero elenco degli attributi e del tipo.
2. restituzione dei **dati** (formato JSON) di tutto il dataset oppure di una specifica parte a seguito dell'inserimento (sempre nella richiesta
GET) di filtri su attributi con operatori *condizionali* o *logici*.
3. restituzione delle **statistiche** sui dati (formato JSON) specificando nella richiesta GET l'attributo rispetto cui si vogliono ottenerle
ed eventualmente si possono inserire filtri (con operatori *condizionali* o *logici*) se non si vuole considerare l'intero dataset, 
ma solo un porzione di esso. <br/>
Riguardo attributi di tipo String si possono ottenere le occorrenze. <br/>
Riguardo attributi di tipo numerico è possibile calcolare:
    * somma
    * massimo
    * minimo
    * media
    * deviazione standard
    * conteggio delle righe <br/>
    
 
**Operatori Logici**

| Operatore | Descrizione |
| --- | --- |
| $or | Operatore logico "or" |
| $and | Operatore logico "and" |

**Operatori Condizionali**

| Operatore | Descrizione |
| --- | --- |
| $eq | == |
| $gt | > |
| $gte | >= |
| $lt | < |
| $lte | <= |
| $bt | >=value <= |

**Restituzione metadati** : _...localhost:8081/metadata_ <br><br>
**Restituzione dati** : _...localhost:8081/data_ <br>
**Restituzione dati filtrati** : _...localhost:8081/data?filter="filtri"_ <br><br>
**Restituzione statistiche** : _...localhost:8081/stats?field="NomeCampo"_ <br>
**Restituzione statistiche con filtri** : _...localhost:8081/stats?field="NomeCampo"&filter="filtri"_ <br><br>
**Restituzione occorrenze** : _...localhost:8081/stats/occorrenze_ <br>
**Restituzione occorrenze filtrate** : _...localhost:8081/stats/occorrenze?filter="filtro"_ <br>


## Come inserire filtri nella richiesta GET

L'inserimento di filtri prevede una sintassi predefinita.<br/>
E' possibile inserire filtri su massimo due campi del dataset (legati da un operatore logico) indicando su di essi l'operatore condizionale e 
il valore/i di filtraggio (nel caso di un operatore $bt vanno specificati gli estermi dell'intervallo separati da una "," ). <br/>
Se si inserisce un campo di tipo String ragionevolmente non va specificato l'operatore condizionale, ma solo il campo e il valore. <br>
Si utilizza come separatore di query il ";" e come sepatore interno alla query i ":". <br>
Se si vogliono inserire i filtri sulla parte delle occorrenze i campi utilizzabili sono solo _CodAteco_ (codice Ateco) e _NumOcc_ (numero occorrenze)
<br><br>**Sintassi**

- Filtro su un solo campo <br> 
__NomeCampo : OperatoreCondizionale : Valore/i__ <br/><br/>
**_Esempio_** <br/>  _NumImp:$gt:30_    --> numero delle imprese maggiore di 30 <br/>
_NumImp:$bt:10,40_     --> numero delle imprese maggiore di 10 e minore di 40 <br/>
_Dim:Micro_ --> imprese con dimensione uguale a "Micro"

- Filtri su 2 campi <br>
__OperatoreLogico ; NomeCampo1 : OperatoreCondizionale1 : Valore/i ; NomeCampo2 : OperatoreCondizionale2 : Valore/i__ <br><br>
**_Esempio_** <br>
_$and;NumImp:$bt:10,40;TotAdd:$gt:15_    --> numero delle imprese compreso fra 10 e 40 e totale addetti maggiore di 15
_$and;NumImp:$bt:10,40;Dim:Micro_  --> numero delle imprese compreso fra 10 e 40 con dimensione "Micro"
<br><br>
## Test 
Una volta avviata l'applicazione, per verificare il funzionamento del programma si possono testare i seguenti link:

| **Link** | **Descrizione** |
| --- | --- |
| http://localhost:8081/metadata | Restituzione metadati |
| http://localhost:8081/data | Restituzione di tutti i dati |
| http://localhost:8081/data?filter=NumImp:$gt:30 | Restituzione di tutti i dati con un numero di imprese maggiore di 30 |
| http://localhost:8081/data?filter=$and;NumImp:$bt:10,40;TotAdd:$gt:15 | Restituzione di tutti i i dati con un numeri di imprese compreso fra 10 e 40 e con un totale di addetti maggiore di 15 |
| http://localhost:8081/data?filter=$and;NumImp:$bt:10,40;Dim:Micro | Restituzione di tutti i dati con un numero di imprese compreso fra 10 e 40 e con dimensione "Micro" |
| http://localhost:8081/stats?field=NumImp | Restituzione delle statistiche rispetto al campo NumImp considerando l'intero dataset |
| http://localhost:8081/stats?field=NumImp&filter=TotAdd:$gt:20 | Restituzione delle statistiche rispetto al campo NumImp considerando solo le imprese con un totale addetti maggiore di 20 |
| http://localhost:8081/stats?field=NumImp&filter=$and;TotAdd:$bt:10,40;Dim:Micro | Restituzione delle statistiche rispetto al campo NumImp considerando sole le imprese con un totale addetti compreso fra 10 e 40 e con dimensione "Micro" |
| http://localhost:8081/stats/occorrenze | Restituisce le occorrenze di tutti i codici ateco specificando anche la descrizione dell'impresa |
| http://localhost:8081/stats/occorrenze?filter=CodAteco:1620 | Restituisce le occorrenze della sola impresa con CodAteco=1620 |
| http://localhost:8081/stats/occorrenze?filter=NumOcc:3 | Restituisce le imprese con un numero di occorrenze pari a 3 |
<br>


 **NOTA** : per una visione migliore si consiglia di utilizzare un programma che simula richieste HTTP come **_Postman_**
