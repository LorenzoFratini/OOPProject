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
	String FileToParse="ImpreseOOP.csv";
	final String DELIMITER=";";
	private ArrayList<Impresa> impresa=new ArrayList<Impresa>();
	String type[]=new String[9];
	String Descrizione[]=new String[9];
	private UseFilter<Impresa> utils=new UseFilter<Impresa>();
	//parsing dei dati dentro il costruttore
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
				impresa.add(appoggio);
				}
		
			fileinput.close();
		}	catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	//------------------------------------------------------------
	
	public ArrayList<Impresa> getData() {
		return impresa;
		
	}
	//-------------------------------------------------------------
	public ArrayList<Metadati> getMetadati() {
		 Field fld[] = Impresa.class.getDeclaredFields();
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
	//-----------------------------------------------------------------
	@Override
	public ArrayList<Impresa> filterField(String fieldName, String operator, Object value)  {
		return (ArrayList<Impresa>) utils.select(impresa, fieldName, operator, value);
	}
	//-------------------------------------------------------------------
	public Statistiche getStats(String fieldName,Collection<Impresa> dati) {
		Statistiche stats=new Statistiche();
		int somma=0;
		int max=Integer.MIN_VALUE;
		int min=Integer.MAX_VALUE;
		double avg=0;
		double std=0;
		double diff_al_quad=0;
		//Analizzo ogni impresa dell'ArrayList e prendo di ognuna solo i valori che mi interessano in base al campo che ho passato come parametro
		for(Impresa item:dati) {
			try {
				Method m = item.getClass().getMethod("get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1),null);
				try {
					Object tmp = m.invoke(item);
					Integer app;
					if (tmp instanceof Number) app=(Integer)tmp; //converto l'oggetto restituito in base al campo in un Integer affinchè posso fare le statistiche
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
	//-----------------------------------------------------
	
	public ArrayList<Occorrenza> ContaOccorrenze(ArrayList<Impresa> dati) {
		int occ=0;
		ArrayList<Occorrenza> out=new ArrayList<Occorrenza>();
		for(int i=0; i<dati.size();i+=occ) {
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
