package statistics;

import java.util.ArrayList;
import java.util.Collection;

import model.Impresa;

/** Claase che serve per restituire  le occorrenze rdel CodAteco e della descrizione delle imprese del dataset.
 * @author Lorenzo Iacopini & Lorenzo Fratini.
 *
 */
public class Occorrenza {
	private String CodAteco;
	private String Descrizione;
	private int NumOcc;
	
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
