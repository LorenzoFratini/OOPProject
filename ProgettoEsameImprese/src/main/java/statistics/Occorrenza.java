package statistics;

import java.util.ArrayList;
import java.util.Collection;

import model.Impresa;

public class Occorrenza {
	private String valore;
	private int num_occorrenze;
	
	public String getValore() {
		return valore;
	}
	public void setValore(String valore) {
		this.valore = valore;
	}
	public int getNum_occorrenze() {
		return num_occorrenze;
	}
	public void setNum_occorrenze(int num_occorrenze) {
		this.num_occorrenze = num_occorrenze;
	}
	
	public int ContaOccorrenze(Collection<Impresa> dati) {
		int occ=0;
		ArrayList<Impresa> imp=new ArrayList<Impresa>(dati);
		for(int i=0; i<dati.size();i+=occ) {
			for(int j=i;j<dati.size();j++) {
				if(imp.get(i).getCodAteco().equals(imp.get(j).getCodAteco())) occ++;
			}
			
		}
		return occ;
	}
	
}
