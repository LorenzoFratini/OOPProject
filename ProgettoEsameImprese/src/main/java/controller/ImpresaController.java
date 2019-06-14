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
	 
	//metodo che serve per controllare se il campo rispetto cui si richiedono dati/statistiche è corretto
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
	
	//metodo che serve per controllare se gli operatori logici e matematici sono corretti
	private void VerificaOperatore(String operatore) {
		if(!(operator.equals("$eq")||operator.equals("$gt")||operator.contentEquals("$gte")||operator.equals("$lt")||operator.equals("$lte")||operator.equals("$bt") || operator.equals("$and") || operator.equals("$or") ||operator.equals("null") )) throw new RuntimeException("ERROR: operatore inserito non valido");
	}
	
	
	//Metodo per il parse della querystring
	public void ParseQuery(String query) {
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
	
	
	
	//metodo per gestire il $bt
	public Collection<Impresa> CollectionFiltrata(String fieldname,String operator){
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
	
	@GetMapping(value="/metadata") 
	public ArrayList<Metadati> getMetadati(){
		return impserv.getMetadati();
	}
	
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
	/*@GetMapping("stats/occorrenze") 
	public ArrayList<Occorrenza> getOccorrenze() {
		return impserv.ContaOccorrenze(impserv.getData());
	}*/
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
			/*ArrayList<Impresa>tuttiidati=new ArrayList<Impresa>(impserv.getData());//tutte le imprese del csv
			ArrayList<Impresa>out=new ArrayList<Impresa>();//qui ci andranno solo quelle che hanno il cod ateco richiesto
			query=query.replaceAll("\\s", "");
			String[] tokenquery=query.split(":");
			String codatecoscelto=tokenquery[0];
			for(int i=0;i<tuttiidati.size();i++) {
				Impresa tmp=new Impresa();
			if(codatecoscelto.equals(tuttiidati.get(i).getCodAteco())) {
				tmp.setCodAteco(tuttiidati.get(i).getCodAteco());
				tmp.setDescrizione(tuttiidati.get(i).getDescrizione());
				tmp.setDim(tuttiidati.get(i).getDim());
				tmp.setNumImp(tuttiidati.get(i).getNumImp());
				tmp.setTotAdd(tuttiidati.get(i).getTotAdd());
				tmp.setTotDip(tuttiidati.get(i).getTotDip());
				tmp.setTotExt(tuttiidati.get(i).getTotExt());
				tmp.setTotInd(tuttiidati.get(i).getTotInd());
				tmp.setTotInt(tuttiidati.get(i).getTotInt());
				out.add(tmp);		
			}}
			if(out.isEmpty())throw new RuntimeException("ERROR: nessuna impresa corrisponde al CodAteco inserito");
			return impserv.ContaOccorrenze(out);//in realtà gli passo poche cose
				*/
			
}
		/*@GetMapping("stats/occorrenze1")
		public ArrayList<Occorrenza> getOccorrenze1(@RequestParam(name="Num_occorr",required=false,defaultValue="null") String query) {
			if(query.equals("null"))return impserv.ContaOccorrenze(impserv.getData());
			else {
				ArrayList<Impresa>tuttiidati=new ArrayList(impserv.getData());
				ArrayList<Occorrenza>occ= new ArrayList<Occorrenza>(impserv.ContaOccorrenze(tuttiidati));
			    ArrayList<Occorrenza>out=new ArrayList<Occorrenza>();
				query=query.replaceAll("\\s", "");
				String[] tokenquery=query.split(":");
				String numoccscelto=tokenquery[0];
				for(int i=0;i<occ.size();i++) {
					Occorrenza tmp= new Occorrenza();
                        if(numoccscelto.equals(occ.get(i).getNum_occorrenze())) {
						tmp.setCodAteco(occ.get(i).getCodAteco());
						tmp.setDescrizione(occ.get(i).getDescrizione());
						tmp.setNum_occorrenze(occ.get(i).getNum_occorrenze());
						out.add(tmp);	
					}			
				}
				if(out.isEmpty())throw new RuntimeException("ERROR: nessuna impresa è presente il numero di  volte inserito");
				return impserv.ContaOccorrenze(out);//problema
			}	*/	
	



