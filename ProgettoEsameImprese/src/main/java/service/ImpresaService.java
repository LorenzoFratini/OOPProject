package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import filter.Filter;
import filter.UseFilter;
import model.Impresa;
import model.Metadati;
import statistics.Occorrenza;
import statistics.Statistiche;

@Service 
public class ImpresaService implements Filter<Impresa,Object>{
	
	//Attributi
	
	private String FileToParse="ImpreseOOP.csv";
	final String DELIMITER=";";
	private ArrayList<Impresa> imprese=new ArrayList<Impresa>();
	private String type[]=new String[9];
	private String Descrizione[]=new String[9];//array che verrà utilizzato per memorizzare le info della prima riga del dataset
	private UseFilter<Impresa> utils=new UseFilter<Impresa>();
	
	//Metodi
	
	/**Effettua il parsing dei dati, all'atto della creazione dell'oggetto, memorizzando le informazioni dentro la classe {@link Impresa} 
	 * 
	 **/
	public ImpresaService() {
		try {
			Scanner fileinput=new Scanner(new BufferedReader(new FileReader(FileToParse)));
			//faccio il parse della prima riga per memorizzare la descrizione di ogni colonna
			String ln=fileinput.nextLine();
			String[] tk=ln.split(DELIMITER);
			for(int i=0;i<9;i++) {
				Descrizione[i]=tk[i];
			}
			while(fileinput.hasNextLine()) {
				String line=fileinput.nextLine();//legge l'intera riga
				String[] token=line.split(DELIMITER);
				Impresa appoggio=new Impresa();
				appoggio.setCodAteco(token[0]);
				appoggio.setDescrizione(token[1]);
				appoggio.setDim(token[2]);
				appoggio.setNumImp(Integer.parseInt(token[3]));
				appoggio.setTotAdd(Integer.parseInt(token[4]));
				appoggio.setTotDip(Integer.parseInt(token[5]));
				appoggio.setTotInd(Integer.parseInt(token[6]));
				appoggio.setTotExt(Integer.parseInt(token[7]));
				appoggio.setTotInt(Integer.parseInt(token[8]));
				imprese.add(appoggio);
				}
		
			fileinput.close();
		}	catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**  
	 * @return <strong>impresa</strong> Restituisce un ArrayList di {@link Impresa}.
	 */
	public ArrayList<Impresa> getData() {
		return imprese;
		
	}
		
	/**Metodo utilizzato per la restituzione dei {@link Metadati} ovvero nomi dei campi e del loro tipo della classe {@link Impresa}.
	 * @return<strong>metdat</strong> Restituisce un ArrayList di {@link Metadati}.
	 */
	public ArrayList<Metadati> getMetadati() {
		 Field fld[] = Impresa.class.getDeclaredFields();//array che memorizza i nomi dei campi della classe impresa e il loro tipo
		 ArrayList<Metadati> metdat=new ArrayList<Metadati>();
		 Impresa imp=new Impresa();
		 for (int i = 0; i < fld.length; i++)
        {
			 Metadati tmp=new Metadati();
			 tmp.setAlias(fld[i].getName()); //ricavo il nome del campo
			 tmp.setSourceField(Descrizione[i]);
			 //ricavo il tipo di dato (i primi 3 so gia che sono di tipo string)
			 if(i<3) tmp.setType(fld[i].getType().getTypeName().substring(10));
       	 		else tmp.setType(fld[i].getType().getTypeName());
			 metdat.add(tmp);
        }                
		
		return metdat;
	} 
	

	/** {@link Filter}
	 *
	 */
	@Override
	public ArrayList<Impresa> filterField(String fieldName, String operator, Object value)  {
		return (ArrayList<Impresa>) utils.select(imprese, fieldName, operator, value);
	}
	
	
	/**Metodo per la restituzione delle statistiche rispetto un campo
	 * @param fieldName Nome del campo rispetto cui si vogliono ottenere le statistiche
	 * @param dati Collection contenente gli oggetti  rispetto cui si calcolano le statistiche 
	 * 		(somma, massimo, minimo, media e deviazione standard). 
	 * @return <strong>stats</strong> Oggetto della classe {@link Statistiche} contenente tutte le statistiche rispetto il campo
	 * 			desiderato.
	 * @throws IllegalAccessException se si vuole ottenere un metodo get di un campo inesistente.
	 * @throws IllegalArgumentException se il nome del campo passato è errato.
	 * @throws NoSuchMethodException se il metodo get ottenuto a seguito del passaggio del campo è inesistente. 
	 * @throws ArithmetichException se la dimensione della Collection che passo come parametro è nulla.
	 */
	public Statistiche getStats(String fieldName,Collection<Impresa> dati) {
		Statistiche stats=new Statistiche();
		int somma=0;
		int max=Integer.MIN_VALUE; //affinchè nel primo confronto che verrà effettuato il valore di tale variabile sarà cambiato
		int min=Integer.MAX_VALUE; //affinchè nel primo confronto che verrà effettuato il valore di tale variabile sarà cambiato
		double avg=0;
		double std=0;
		double diff_al_quad=0;
		//Analizzo ogni impresa della Collection e prendo di ognuna solo i valori che mi interessano in base al campo che ho passato come parametro
		for(Impresa item:dati) {
			try {
				Method m = item.getClass().getMethod("get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1),null);
				try {
					Object tmp = m.invoke(item);
					Integer app;
					if (tmp instanceof Number) app=(Integer)tmp; //converto l'oggetto restituito in base al campo in un Integer affinchè posso calcolare le statistiche
						else return null; //da aggiungere un'eccezione che gestisce il caso in cui si richiedono statistiche per una stringa
					somma=stats.Somma(app, somma);
					max=stats.Max(app, max);
					min=stats.Min(app, min);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}		
		}
		try {
			avg=(double)somma/dati.size();
		} catch(ArithmeticException e) {
			e.printStackTrace();
		}
		stats.setSum(somma);
		stats.setMax(max);
		stats.setMin(min);
		stats.setAvg(avg);
		stats.setStd(stats.std(avg, dati, fieldName));
		stats.setCount(dati.size());
		stats.setField(fieldName);
		return stats;
	}
	
	/** Metodo per il conteggio delle occorrenze delle imprese con stesso CodAteco e Descrizione.
	 * @param dati ArrayList di oggetti {@link Impresa} di cui si vogliono contare le occorrenze. 
	 * @return <strong>out</strong> ArrayList di oggetti {@link Occorrenza}.
	 */
	public ArrayList<Occorrenza> ContaOccorrenze(ArrayList<Impresa> dati) {
		int occ=0;
		ArrayList<Occorrenza> out=new ArrayList<Occorrenza>(); 
		for(int i=0; i<dati.size();i+=occ) { //Essendo il dataset ordinato per CodAteco incremento il contatore di una quantità pari all'occorrenza
			occ=0;
			Occorrenza tmp=new Occorrenza(); 
			for(int j=i;j<dati.size();j++) {
				if(dati.get(i).getCodAteco().equals(dati.get(j).getCodAteco())) occ++;
			}
			tmp.setCodAteco(dati.get(i).getCodAteco());
			tmp.setDescrizione(dati.get(i).getDescrizione());
			tmp.setNumOcc(occ);
			out.add(tmp);
		}
		
		return out;
	}
	
	
}
