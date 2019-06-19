package controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Logger;
import model.Impresa;
import model.Metadati;
import service.ImpresaService;
import statistics.Occorrenza;
import statistics.Statistiche;

/**
 * @author Lorenzo Fratini & Lorenzo Iacopini
 * @version 1.0
 *
 */

@RestController
@RequestMapping
public class ImpresaController {
	
	//Attributi
	
	private String fieldName;
	private String operator;
	private String value;
	private String[] values=new String[2];
	private HashSet<Impresa> hs3;
	private HashSet<Impresa> hs4;
	
	@Autowired
	ImpresaService impserv;
	
	//Metodi
	
	
	/**Metodo ausiliario per verificare se il nome del campo inserito è corretto.
	 * @param fieldName Nome del campo che si vuole verificare
	 * @throws RuntimeException se il nome del campo inserito non è valido.
	 */
	private void VerificaCampo(String fieldName) {
		ArrayList<Metadati> metdat=impserv.getMetadati();
		boolean trovato=false;
		int i=0;
		while(!trovato && i<metdat.size() ) {
			if ((fieldName.substring(0,1).toUpperCase()+fieldName.substring(1)).equals(metdat.get(i).getAlias()) || fieldName.equals("NumOcc")||fieldName.equals("numOcc")) trovato=true;
			i++;
		}
		if (!trovato) {
			throw new RuntimeException("ERROR: alias non corretto");
		}
		
	}
	
	
	/**Metodo ausiliario se l'operatore utilizzato nella richiesta è valido.
	 * @param operatore Operatore che si vuole verificare.
	 * @throws RuntimeException se l'operatore inserito non è corretto.
	 */
	private void VerificaOperatore(String operatore) {
		if(!(operator.equals("$eq")||operator.equals("$gt")||operator.contentEquals("$gte")||operator.equals("$lt")||operator.equals("$lte")||operator.equals("$bt") || operator.equals("$and") || operator.equals("$or") ||operator.equals("null") )) throw new RuntimeException("ERROR: operatore inserito non valido");
	}
	
	
	/**Metodo che esegue il parsing della QueryString utilizzata nella richiesta andando a ricavare il nome del campo rispetto cui si vogliono ottenere 
	 * informazioni, l'operatore e il valore per il confronto.
	 * @param query Ciò che viene scritto nella richiesta
	 * @throws RuntimeException se nella richiesta non vengono utilizzati i ":" come separatore di valori <br>
	 * Esempio)  NumImp:$gt:15   (corretto)
	 * 	
	 * 
	 */
	private void ParseQuery(String query) {
		if(!query.contains(":")) throw new RuntimeException("ERROR: Query non contenente il simbolo "+" : "+" come separatore di valori");
		String[] token=query.split(":");
		VerificaCampo(token[0]);
		this.fieldName=token[0];
		if(this.fieldName.equals("Dim")||this.fieldName.equals("dim") || this.fieldName.equals("CodAteco") || this.fieldName.equals("codAteco") || this.fieldName.equals("Descrizione") || this.fieldName.equals("descrizione") || this.fieldName.equals("NumOcc") || this.fieldName.equals("numOcc")) {
			this.operator="null"; //non ha senso definire operatori matematici per le stringhe
			this.value=token[1];
		} else {
			this.operator=token[1];
			VerificaOperatore(operator);
			//Gestisco il caso in cui si passano come valori degli estremi di riferimento (vengono separati con delle virgole)
			String appoggio[]=token[2].split(",");
			for(int i=0;i<appoggio.length;i++) {
			this.values[i]=appoggio[i];
			}
		}	
		
}
	
	/**Metodo per ottenere la Collection filtrata
	 * @param fieldname Nome del campo rispetto cui filtrare
	 * @param operator Operatore del confronto
	 * @return <strong>hs1</strong> HashSet di {@link Impresa} contenente le imprese che soddisfano i filtri.
	 */
	private Collection<Impresa> CollectionFiltrata(String fieldname,String operator){
		ArrayList<Impresa> out1=new ArrayList<Impresa>();
	    ArrayList<Impresa> out2=new ArrayList<Impresa>();
	    HashSet<Impresa> hs1;
	    HashSet<Impresa> hs2;
	    VerificaOperatore(operator);
		switch(operator) {
		//Separo il caso in cui inserisco come operatore $bt dagli altri casi banali
		case "$bt": {
			//Considero l'operatore $bt come intersezione fra $gt e $lt
			out1=impserv.filterField(fieldName, "$gt", Integer.parseInt(values[0]));
			out2=impserv.filterField(fieldName, "$lt", Integer.parseInt(values[1]));	
			hs1=new HashSet<Impresa>(out1);
			hs2=new HashSet<Impresa>(out2);
			hs1.retainAll(hs2);
			return hs1;
			}
		//Casi banali dove non inserisco operatori oppure inserisco operatori semplici
		default: {
			if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
				out1= impserv.filterField(fieldName, operator, Integer.parseInt(values[0]));
				hs1=new HashSet<Impresa>(out1);
			} else {
				out1= impserv.filterField(fieldName, operator, value);
				hs1=new HashSet<Impresa>(out1);
			}
			return hs1;
		}
		}		
}
	
	
	/**Metodo che restituisce i <strong>metadati</strong> quando nella richiesta HTTP viene inserito "/metadata"
	 * @return {@link ImpresaService#getMetadati()}
	 */
	@GetMapping(value="/metadata") 
	public ArrayList<Metadati> getMetadati(){
		return impserv.getMetadati();
	}
	
	/**Metodo che serve per restituire i dati; sia dell'intero dataset sia nel caso di passaggio di filtri.
	 * @param query Ciò che viene scritto dopo il parametro <strong>filter</strong> nella richiesta HTTP.
	 * @return {@link ImpresaService#getData()} nel caso in cui non si inserisce alcun filtro.<br>
	 * 			<strong>hs3</strong> HashSet contenente le imprese che soddisfano i filtri passati durante la richiesta.
	 */
	@GetMapping(value="/data") 
	public Collection<Impresa> getData(@RequestParam(name="filter",required=false,defaultValue="null") String query) {
		if(query.equals("null")) return impserv.getData();
		else {
			//Elimino eventuali spazi bianchi nella query contenente i filtri
			query=query.replaceAll("\\s", "");
			//Inizio parse della Stringa in cui sono presenti i filtri
			String[] tokenquery=query.split(";");
			String logicalop=tokenquery[0];
			//Verifico se l'operatore logico è un $and oppure un $or
			if((logicalop.equals("$and")|| logicalop.equals("$or"))) {
				//Continuo il parse della Stringa in cui sono presenti i filtri
				String query1=tokenquery[1];
				String query2=tokenquery[2];
				//Faccio il parsing della prima query e restituisco una collezione contenente i dati che soddisfano i filtri
				ParseQuery(query1);
				hs3=new HashSet<Impresa>(CollectionFiltrata(fieldName,operator));
				//Faccio il parsing della seconda query e restituisco una collezione contente i dati che soddisfano i filtri
				ParseQuery(query2);
				hs4=new HashSet(CollectionFiltrata(fieldName,operator));
				//Se l'operatore è un $or faccio la fusione delle due Collection
				if(logicalop.equals("$or"))  {
					hs3.addAll(hs4);
					return hs3;
				}
				
				//Se l'operatore è un $and faccio la fusione solo degli elementi comuni alle due Collection 
				if(logicalop.equals("$and")) {
					hs3.retainAll(hs4);
					return hs3;
				}
			//Gestisco il caso in cui non ci sono operatori logici e quindi si ha una sola query in cui vi sono i filtri
		     }else {
		    	  logicalop=null;
		    	  ParseQuery(query);
		    	  hs3=new HashSet(CollectionFiltrata(fieldName,operator));
		    	  return hs3;
		      }
			return null;
		}	
	}
	
	
	
	/**Metodo che serve per la restituzione delle statistiche {somma, massimo, minimo, media, deviazione standard} rispetto un certo campo.
	 * @param fieldStats Nome del campo rispetto cui si vogliono calcolare le statistiche
	 * @param query Stringa contenente i filtri per il calcolo delle statistiche solo rispetto alcune imprese 
	 * @return {@link ImpresaService#getStats(String, Collection)} <br>
	 * 			Nel caso in cui nella richiesta non vengono specificati filtri si considerano tutte le imprese<br>
	 * 			Nel caso in cui nella richiesta vengono specificati dei filtri al metodo che calcola le stastiche si passa
	 * 			una Collection di {@link Impresa} contenente solo le imprese che soddisfano i filtri.
	 * 			
	 * 			
	 */
	@GetMapping(value="/stats")
	public Statistiche getStatisticheFiltrate(@RequestParam(name="field") String fieldStats,
	                                          @RequestParam(name="filter",required=false,defaultValue="null") String query) {
	//Elimino eventuali spazi bianchi nel campo rispetto cui si richiedono le statistiche
	fieldStats=fieldStats.replaceAll("\\s", "");
	VerificaCampo(fieldStats);
	if(query.equals("null")) return impserv.getStats(fieldStats,impserv.getData());
	else {
		query=query.replaceAll("\\s", "");
		String[] tokenquery=query.split(";");
		String logicalop=tokenquery[0];
		if((logicalop.equals("$and")|| logicalop.equals("$or"))) {
			String query1=tokenquery[1];
			String query2=tokenquery[2];
			ParseQuery(query1);
			hs3=new HashSet<Impresa>(CollectionFiltrata(fieldName,operator));
			ParseQuery(query2);
			hs4=new HashSet(CollectionFiltrata(fieldName,operator));
		if(logicalop.equals("$or"))  {
			hs3.addAll(hs4);
		}
		 
		if(logicalop.equals("$and")) {
			hs3.retainAll(hs4);
		}
		return impserv.getStats(fieldStats, hs3);
		
     }else {
    	  logicalop=null;
    	  ParseQuery(query);
    	  hs3=new HashSet(CollectionFiltrata(fieldName,operator));
		
		return impserv.getStats(fieldStats,hs3);
		
     	}
	}
}
	
	
	/**Metodo per la restituzione delle occorrenze del Codice Ateco delle imprese.
	 * Vi è anche la possibilità di inserire un filtro se si vogliono stampare le occorenze delle aziendo rispetto un solo Codice Ateco 
	 * oppure rispetto ad un certo numero di occorrenze prefissato.
	 * @param query Stringa contenente Codice Ateco o numero occorrenze rispetto cui si vogliono ottenere i dati
	 * @return {@link ImpresaService#ContaOccorrenze(ArrayList)} se non viene specificato nessun filtro <br>
	 * 			<strong>out</strong> Collection di {@link Occorrenza} che soddisfano il filtro inserito.
	 * @throws IllegalAccessException se si vuole ottenere un metodo get di un campo inesistente.
	 * @throws IllegalArgumentException se il nome del campo passato è errato.
	 * @throws NoSuchMethodException se il metodo get ottenuto a seguito del passaggio del campo è inesistente.
	 * @throws RuntimeException se non ci sono risultati per il Codice Ateco o il numero di occorrenze inserito nel filtro.
	 */
	@GetMapping("stats/occorrenze") 
	public ArrayList<Occorrenza> getOccorrenze(@RequestParam(name="filter",required=false,defaultValue="null") String query) {
		if(query.equals("null"))return impserv.ContaOccorrenze(impserv.getData());
		else {
			query=query.replaceAll("\\s", "");
			ParseQuery(query);
			if(!(fieldName.equals("CodAteco") || fieldName.equals("codAteco") || fieldName.equals("NumOcc") || fieldName.equals("numOcc"))) throw new RuntimeException("Impossibile restituire occorrenze, riprova!");
			//ArrayList con tutte le occorrenze
			ArrayList<Occorrenza> app=new ArrayList<Occorrenza>(impserv.ContaOccorrenze(impserv.getData()));
			//ArrayList in cui memorizzo l'uscita
			ArrayList<Occorrenza> out=new ArrayList<Occorrenza>();
			//Scorro l'intera lista contenente tutti gli oggetti di tipo Occorrenza
			for(Occorrenza item:app) {
				try {
					Method m=item.getClass().getMethod("get"+fieldName.substring(0,1)+fieldName.substring(1), null);
					try {
						Object tmp=m.invoke(item);
						//Ciò che sta dentro tmp può essere di tipo Stringa(CodAteco) oppure di tipo int(NumOcc) quindi devo considerare due casi distrinti
						if (tmp instanceof String) {
							tmp=(String)tmp;
							if(tmp.equals(value)) {
								out.add(item);
							} 
						}else {
							tmp=((Integer)tmp).intValue();
							if(tmp.equals(Integer.parseInt(value))) {
								out.add(item);
							} 
						}
						
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
				catch (SecurityException e) {
				e.printStackTrace();
			}
			}
			if (out.isEmpty()) throw new RuntimeException("ERROR: CodAteco non esistente oppure NumOcc non esistente");
			return out;
		}
	} 
		
}
		



