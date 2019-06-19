package statistics;

import java.util.ArrayList;
import java.util.Collection;

import model.Impresa;

/** Claase che serve per restituire le occorrenze del CodAteco e della Descrizione delle imprese del dataset.
 * @author Lorenzo Iacopini & Lorenzo Fratini.
 * @version 1.0
 */
public class Occorrenza {
	
	//Attributi
	
	private String CodAteco;
	private String Descrizione;
	private int NumOcc;
	
	//Metodi
	
	public String getCodAteco() {
		return CodAteco;
	}
	public void setCodAteco(String CodAteco) {
		this.CodAteco = CodAteco;
	}
	public int getNumOcc() {
		return NumOcc;
	}
	public void setNumOcc(int NumOcc) {
		this.NumOcc = NumOcc;
	}
	public String getDescrizione() {
		return Descrizione;
	}
	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}
	
	
	
}
