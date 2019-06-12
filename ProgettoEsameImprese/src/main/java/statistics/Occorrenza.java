package statistics;

import java.util.ArrayList;

import model.Impresa;

public class Occorrenza {
	
		private String Descrizione;
		private int Occorrenza;
		
		public Occorrenza() {
			this.Descrizione="";
			this.Occorrenza=0;
		}
		
		public String getDescrizione() {
			return Descrizione;
		}
		public void setDescrizione(String descrizione) {
			Descrizione = descrizione;
		}
		public int getOcc() {
			return Occorrenza;
		}
		public void setOcc(int occ) {
			this.Occorrenza = occ;
		}
		
		
		public ArrayList<Occorrenza> calcoloOccorrenza(ArrayList<Impresa> impresa,String field) {
			Converter temp=new Converter();
			ArrayList<Occorrenza> occorrenze=new ArrayList<Occorrenza>();
			int prog=0;
			for(int i=0; i<impresa.size();i+=prog) {
				Occorrenza occ=new Occorrenza();
				int contatore=1;
				for(int j=i+1;j<impresa.size();j++) {
					if(temp.converterstringa(field, impresa.get(i)).equals(temp.converterstringa(field, impresa.get(j)))) contatore++;
				}
				prog=contatore;
				occ.setOcc(contatore);
				occ.setDescrizione(temp.converterstringa(field, impresa.get(i)));
				occorrenze.add(occ);
			}		
			return occorrenze;

}
}
