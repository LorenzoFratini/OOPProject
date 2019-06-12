package statistics;

import java.util.ArrayList;
import java.util.HashMap;

import model.Impresa;

public class DefineOccorrenza {
	//Questo metodo calcola le occorrenze di tutte le attività economiche del dataset
		public HashMap<String,Occorrenza> setOccorrenza(ArrayList<Impresa> impresa) {
			HashMap<String,Occorrenza> occ=new HashMap<String,Occorrenza>();
			for(int i=0; i<impresa.size();i++) {
				if(!(occ.containsKey(impresa.get(i).getCodAteco()))) {
				Occorrenza app=new Occorrenza();
				int contatore=1;
				app.setDescrizione(impresa.get(i).getDescrizione());
				for(int j=i+1; j<impresa.size();j++) {
					if(impresa.get(i).getCodAteco().equals(impresa.get(j).getCodAteco())) {
						contatore++;
					}
				}
				app.setOcc(contatore);
				occ.put(impresa.get(i).getCodAteco(), app);
			}
			}
			return occ;
	}	
	//Questo metodo calcola le occorrenze delle imprese in base alla dimensione che si passa nella richiesta
		public int OccorrenzeDimensione(ArrayList<Impresa> impresa,String field) {
			int contatore=0;
			for(int i=0; i<impresa.size();i++) {
				if(impresa.get(i).getDim().equals(field)) contatore++;
			}
			return contatore;
		}

	}



