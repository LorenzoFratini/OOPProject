package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Impresa;
import model.Metadati;
import service.ImpresaService;
import statistics.Statistiche;

@RestController
@RequestMapping
public class ImpresaController {
	
	private String fieldName;
	private String operator;
	private String value;
	private String[] values=new String[2];
	
	@Autowired
	ImpresaService impserv;
	
	//Definisco dei HashSet dove memorizzare le Collection che derivano dalle due query inserite
	//private ArrayList<Impresa> out1=new ArrayList<Impresa>();
	//private ArrayList<Impresa> out2=new ArrayList<Impresa>();
	//private ArrayList<Impresa> out3=new ArrayList<Impresa>();
	//private ArrayList<Impresa> out4=new ArrayList<Impresa>();
	//private HashSet<Impresa> hs1;
	//private HashSet<Impresa> hs2;
	private HashSet<Impresa> hs3;
	private HashSet<Impresa> hs4;
	
	
	//Metodo per il parse della querystring
	public void ParseQuery(String query) {
		String[] token=query.split(":");
		this.fieldName=token[0];
		if(this.fieldName.equals("Dim")||this.fieldName.equals("dim") || this.fieldName.equals("CodAteco") || this.fieldName.equals("codAteco") || this.fieldName.equals("Descrizione") || this.fieldName.equals("descrizione")) {
			this.operator="null"; //non ha senso definire operatori matematici per le stringhe
			this.value=token[1];
		} else {
			this.operator=token[1];
			//Gestisco il caso in cui si passano come valori degli estremi di riferimento (vengono separati con delle virgole)
			String appoggio[]=token[2].split(",");
			for(int i=0;i<appoggio.length;i++) {
			this.values[i]=appoggio[i];
			}
		}	
	}
	
	
	
	//metodo per gestire il $bt
	public Collection<Impresa> CollectionFiltrata(String fieldname,String operator){
		ArrayList<Impresa> out1=new ArrayList<Impresa>();
	    ArrayList<Impresa> out2=new ArrayList<Impresa>();
	    HashSet<Impresa> hs1;
	    HashSet<Impresa> hs2;
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
	
	@GetMapping(value="/metadata") 
	public ArrayList<Metadati> getMetadati(){
		return impserv.getMetadati();
	}
	
	@GetMapping(value="/data") 
	public Collection<Impresa> getData(@RequestParam(name="filter",required=false,defaultValue="null") String query) {
		if(query.equals("null")) return impserv.getData();
		else {
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
				//Separo il caso in cui inserisco come operatore $bt dagli altri casi banali
			/*	switch(operator) {
					case "$bt": {
						//*if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {
								//Considero l'operatore $bt come intersezione fra $gt e $lt
						//		out1=impserv.filterField(fieldName, "$gt", Integer.parseInt(values[0]));
							//	out3=impserv.filterField(fieldName, "$lt", Integer.parseInt(values[1]));	
							//	 hs1=new HashSet<Impresa>(out1);
							//	hs3=new HashSet<Impresa>(out3);
							//	hs1.retainAll(hs3);
						hs1=(HashSet<Impresa>) OperatorBetween(fieldName,Integer.parseInt(values[0]),Integer.parseInt(values[1]));
					};break;
				//Casi banali dove non inserisco operatori oppure inserisco operatori semplici	
					default:  {
						if(!(fieldName.equals("CodAteco")||fieldName.equals("codateco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
							out1= impserv.filterField(fieldName, operator, Integer.parseInt(values[0]));
							hs1=new HashSet<Impresa>(out1);
						} else {
							out1= impserv.filterField(fieldName, operator, value);
							hs1=new HashSet<Impresa>(out1);
						}
					}
				}*/
				//Faccio il parsing della seconda query e restituisco una collezione contente i dati che soddisfano i filtri
				ParseQuery(query2);
				hs4=new HashSet(CollectionFiltrata(fieldName,operator));
				//Separo il caso in cui inserisco come operatore $bt dagli altri casi banali
				/*switch(operator) {
					case "$bt": {
						//if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {
						//Considero l'operatore $bt come intersezione fra $gt e $lt
						//	out2=impserv.filterField(fieldName, "$gt", Integer.parseInt(values[0]));
						//	out4=impserv.filterField(fieldName, "$lt", Integer.parseInt(values[1]));	
						//	hs2=new HashSet<Impresa>(out2);
						//	hs4=new HashSet<Impresa>(out4);
						//	hs2.retainAll(hs4);
						hs2=(HashSet<Impresa>) OperatorBetween(fieldName,Integer.parseInt(values[0]),Integer.parseInt(values[1]));
					};break;
				//Casi banali dove non inserisco operatori oppure inserisco operatori semplici
					default:  {
						if(!(fieldName.equals("CodAteco")||fieldName.equals("codateco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
							out2= impserv.filterField(fieldName, operator, Integer.parseInt(values[0]));
							hs2=new HashSet<Impresa>(out2);
						} else {
							out2= impserv.filterField(fieldName, operator, value);
							hs2=new HashSet<Impresa>(out2);
						}
					}
					
				}*/
				
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
		    	 /* switch(operator) {
					case "$bt": {
						//if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {
						//		out1=impserv.filterField(fieldName, "$gt", Integer.parseInt(values[0]));
						//		out3=impserv.filterField(fieldName, "$lt", Integer.parseInt(values[1]));	
						//		hs1=new HashSet<Impresa>(out1);
						//		hs3=new HashSet<Impresa>(out3);
						//		hs1.retainAll(hs3);
						hs1=(HashSet<Impresa>) OperatorBetween(fieldName,Integer.parseInt(values[0]),Integer.parseInt(values[1]));
					};break;
					
					default:  {
						if(!(fieldName.equals("CodAteco")||fieldName.equals("codateco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
							out1= impserv.filterField(fieldName, operator, Integer.parseInt(values[0]));
							hs1=new HashSet<Impresa>(out1);
						} else {
							out1= impserv.filterField(fieldName, operator, value);
							hs1=new HashSet<Impresa>(out1);
						}
					}
				}*/
		    	  return hs3;
				/*if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
						out1= impserv.filterField(fieldName, operator, Integer.parseInt(value[0]));//spiegata la conversione
					}else out1= impserv.filterField(fieldName, operator, value[0]);
				return out1;*/
		          
		      }
			return null;
		}	
	}
	//--------------------------------------------------------------------------------
	/*@GetMapping(value="/stats")
	public Statistiche getStatistiche(@RequestParam(name="field") String fieldName) {
		return impserv.getStats(fieldName,impserv.getData());
	}*/
	
	
	@GetMapping(value="/stats")
	public Statistiche getStatisticheFiltrate(@RequestParam(name="field") String fieldName1,
	                                          @RequestParam(name="filter",required=false,defaultValue="null") String query)
	{if(query.equals("null")) return impserv.getStats(fieldName1,impserv.getData());
	else {
		String[] tokenquery=query.split(";");
		String logicalop=tokenquery[0];
		if((logicalop.equals("$and")|| logicalop.equals("$or"))) {
			String query1=tokenquery[1];
			String query2=tokenquery[2];
			ParseQuery(query1);
			hs3=new HashSet<Impresa>(CollectionFiltrata(fieldName,operator));
		ParseQuery(query);
		hs3=new HashSet<Impresa>(CollectionFiltrata(fieldName,operator));
		ParseQuery(query2);
		hs4=new HashSet(CollectionFiltrata(fieldName,operator));
		if(logicalop.equals("$or"))  {
			hs3.addAll(hs4);
		}
		 
		if(logicalop.equals("$and")) {
			hs3.retainAll(hs4);
		}
     }else {
    	  logicalop=null;
    	  ParseQuery(query);
    	  hs3=new HashSet(CollectionFiltrata(fieldName,operator));
		
		return impserv.getStats(fieldName,hs3);
		
	}}
	return null;
}}


