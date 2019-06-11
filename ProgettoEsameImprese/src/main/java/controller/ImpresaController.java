package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import esempioFilter.Person;
import model.Impresa;
import model.Metadati;
import service.ImpresaService;

@RestController
@RequestMapping
public class ImpresaController {
	
	@Autowired
	ImpresaService impserv;
	ArrayList<Impresa> out1=new ArrayList<Impresa>();
	ArrayList<Impresa> out2=new ArrayList<Impresa>();
	
	
	@GetMapping(value="/metadata") 
	public ArrayList<Metadati> getMetadati(){
		return impserv.getMetadati();
	}
	
	@GetMapping(value="/data") 
	public Collection<Impresa> getData(@RequestParam(name="filter",required=false,defaultValue="null") String query) {
		if(query.equals("null")) return impserv.getData();
		else {
			String[] tokenquery=query.split(":");//la stringa viene scandita dai "due punti"
			
			String logicalop=tokenquery[0];//verifico se la prima parola inserita è un or o un and
			if((logicalop.equals("$and")|| logicalop.equals("$or"))) {
			
			String query1=tokenquery[1];
			String query2=tokenquery[2];
			System.out.println(logicalop+" -- "+query1+" -- "+query2);
			//parse query1
			String token[]=query1.split(";");//la query viene scandita dal "punto e virgola"
			String fieldName=token[0];
			String operator=token[1];
			String valuefilter=token[2];
			if(fieldName.equals("null") && operator.equals("null") && valuefilter.equals("null")) out1= impserv.getData();//??
				if(!(fieldName.equals("CodAteco")|| fieldName.equals("Dim")|| fieldName.equals("Descrizione"))) {//se entro nell'if si parla di interi
					out1= impserv.filterField(fieldName, operator, Integer.parseInt(valuefilter));//spiegata la conversione
			}else out1= impserv.filterField(fieldName, operator, valuefilter);
				//parse query2
				String token2[]=query2.split(";");//la query viene scandita dal "punto e virgola"
				String fieldName2=token2[0];
				String operator2=token2[1];
				String valuefilter2=token2[2];
				if(fieldName2.equals("null") && operator2.equals("null") && valuefilter2.equals("null")) out2= impserv.getData();//??
					if(!(fieldName2.equals("CodAteco")|| fieldName2.equals("Dim")|| fieldName2.equals("Descrizione"))) {//se entro nell'if si parla di interi
						out2 = impserv.filterField(fieldName2, operator2, Integer.parseInt(valuefilter2));//spiegata la conversione
				}else out2= impserv.filterField(fieldName2, operator2, valuefilter2);
				
		      }else {
		    	  logicalop=null;//in caso non ci sia un or o un and non abbiamo un operatore logico e ha senso solo considerare la query1
		          String query1=tokenquery[1];
		          String token[]=query1.split(";");//la query viene scandita dal "punto e virgola"
					String fieldName=token[0];
					String operator=token[1];
					String valuefilter=token[2];
					if(fieldName.equals("null") && operator.equals("null") && valuefilter.equals("null")) out1= impserv.getData();//??
						if(!(fieldName.equals("CodAteco")|| fieldName.equals("Dim")|| fieldName.equals("Descrizione"))) {//se entro nell'if si parla di interi
							out1= impserv.filterField(fieldName, operator, Integer.parseInt(valuefilter));//spiegata la conversione
					}else out1= impserv.filterField(fieldName, operator, valuefilter);
		          
		      }
			HashSet<Impresa> hs = new HashSet<Impresa>(out1);//hash set insieme di oggetti non ordinati, sono gli arraylist filtrati
			HashSet<Impresa> hs2 = new HashSet<Impresa>(out2);
			if(logicalop.equals("$or")) return hs.addAll(hs2);
			if(logicalop.equals("$and")) return hs.retainAll(hs2);
			
			
				
			//ArrayList<Impresa> iout=impserv.filterField(fieldName, operator, value1);
			//ArrayList<Impresa>
		}
		
		//if(fieldName.equals("null") && operator.equals("null") && value.equals("null")) return impserv.getData();
			//else return impserv.filterField(fieldName, operator, value);
		
	}
	

}
