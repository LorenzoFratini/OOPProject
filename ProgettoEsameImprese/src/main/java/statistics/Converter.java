package statistics;

import model.Impresa;

//serve ad ottenere il valore del campo richiesto
public class Converter {
	public int converter(String field,Impresa imp) {
		switch(field) {
			case "NumImp": return imp.getNumImp();
			case "TotInd": return imp.getTotInd();
			case "TotDip": return imp.getTotDip();
			case "TotExt": return imp.getTotExt();
			case "TotInt": return imp.getTotInt();
			default: throw new RuntimeException("alias inserito non valido!\nControlla: http://localhost:8081/metadata");
		}
	}
	
	public String converterstringa(String field,Impresa imp) {
		switch(field) {
			case "CodAteco": return imp.getCodAteco();
			case "Descrizione": return imp.getDescrizione();
			case "Dim": return imp.getDim();
			default: throw new RuntimeException("alias inserito non valido!\nControlla: http://localhost:8081/metadata");
		}
	}
	

}
