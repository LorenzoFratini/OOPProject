package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import filter.Filter;
import filter.UseFilter;
import model.Impresa;
import model.Metadati;

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
	
	public ArrayList<Impresa> getData() {
		return impresa;
		
	}
	
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
	
	@Override
	public ArrayList<Impresa> filterField(String fieldName, String operator, Object value) {
		return (ArrayList<Impresa>) utils.select(impresa, fieldName, operator, value);
	}
}
