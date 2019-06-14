package statistics;

import java.util.ArrayList;
import java.util.Collection;

import model.Impresa;

public class Occorrenza {
	private String CodAteco;
	private String Descrizione;
	private int num_occorrenze;
	
	public String getCodAteco() {
		return CodAteco;
	}
	public void setCodAteco(String CodAteco) {
		this.CodAteco = CodAteco;
	}
	public int getNum_occorrenze() {
		return num_occorrenze;
	}
	public void setNum_occorrenze(int num_occorrenze) {
		this.num_occorrenze = num_occorrenze;
	}
	public String getDescrizione() {
		return Descrizione;
	}
	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}
	
	
	
}
