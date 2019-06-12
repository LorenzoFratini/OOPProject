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

@RestController
@RequestMapping
public class ImpresaController {
	
	private String fieldName;
	private String operator;
	private String value;
	
	@Autowired
	ImpresaService impserv;
	
	ArrayList<Impresa> out1=new ArrayList<Impresa>();
	ArrayList<Impresa> out2=new ArrayList<Impresa>();
	
	//Metodo per il parse della querystring
	public void ParseQuery(String query) {
		String[] token=query.split(":");
		this.fieldName=token[0];
		if(this.fieldName.equals("Dim")||this.fieldName.equals("dim") || this.fieldName.equals("CodAteco") || this.fieldName.equals("codAteco") || this.fieldName.equals("Descrizione") || this.fieldName.equals("descrizione")) {
			this.operator=null; //non ha senso definite operatori matematici per le stringhe
			this.value=token[1];
		} else {
			this.operator=token[1];
			this.value=token[2];
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
				//System.out.println(logicalop+" -- "+query1+" -- "+query2);
				//Faccio il parsing della prima query e restituisco una collezione contenente i dati che soddisfano i filtri
				ParseQuery(query1);
				//System.out.println(fieldName+" "+operator+" "+value);
				out1=impserv.filterField(fieldName, operator, value);
				if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
					out1= impserv.filterField(fieldName, operator, Integer.parseInt(value));
				}else out1= impserv.filterField(fieldName, operator, value);
				
				//Faccio il parsing della seconda query e restituisco una collezione contente i dati che soddisfano i filtri
					ParseQuery(query2);
				//System.out.println(fieldName+" "+operator+" "+value);
					if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
						out2 = impserv.filterField(fieldName, operator, Integer.parseInt(value));//spiegata la conversione
					}else out2= impserv.filterField(fieldName,operator,value);
				
			//Gestisco il caso in cui non ci sono operatori logici e quindi si ha una sola query in cui vi sono i filtri
		     }else {
		    	  logicalop=null;
		    	  ParseQuery(query);
				if(!(fieldName.equals("CodAteco")||fieldName.equals("codAteco")||fieldName.equals("dim")||fieldName.equals("Dim")|| fieldName.equals("Descrizione") || fieldName.equals("descrizione"))) {//se entro nell'if si parla di interi
						out1= impserv.filterField(fieldName, operator, Integer.parseInt(value));//spiegata la conversione
					}else out1= impserv.filterField(fieldName, operator, value);
				return out1;
		          
		      }
			
			//Definisco due HashSet dove memorizzare le due Collection che derivano dalle due query inserite
			HashSet<Impresa> hs = new HashSet<Impresa>(out1);
			HashSet<Impresa> hs2 = new HashSet<Impresa>(out2);
			
			//Se l'operatore è un $or faccio la fusione delle due Collection
			if(logicalop.equals("$or"))  {
				hs.addAll(hs2);
				return hs;
			}
			
			//Se l'operatore è un $and faccio la fusione solo degli elementi comuni alle due Collection 
			if(logicalop.equals("$and")) {
				hs.retainAll(hs2);
				return hs;
			}
			return null;
		}	
	}
}
