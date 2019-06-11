package model;

public class Impresa {
	private String CodAteco;
	private String Descrizione;
	private String Dim;
	private int NumImp;
	private int TotAdd;
	private int TotDip;
	private int TotInd;
	private int TotExt;
	private int TotInt;

	public int getTotAdd() {
		return TotAdd;
	}
	public void setTotAdd(int totAdd) {
		TotAdd = totAdd;
	}
	public Impresa() {
	}
	public String getCodAteco() {
		return CodAteco;
	}
	public void setCodAteco(String codAteco) {
		CodAteco = codAteco;
	}
	public String getDescrizione() {
		return Descrizione;
	}
	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}
	public String getDim() {
		return Dim;
	}
	public void setDim(String dim) {
		Dim = dim;
	}
	public int getNumImp() {
		return NumImp;
	}
	public void setNumImp(int numImprese) {
		NumImp = numImprese;
	}
	public int getTotDip() {
		return TotDip;
	}
	public void setTotDip(int totDip) {
		TotDip = totDip;
	}
	public int getTotInd() {
		return TotInd;
	}
	public void setTotInd(int totInd) {
		TotInd = totInd;
	}
	public int getTotExt() {
		return TotExt;
	}
	public void setTotExt(int totExt) {
		TotExt = totExt;
	}
	public int getTotInt() {
		return TotInt;
	}
	public void setTotInt(int totInt) {
		TotInt = totInt;
	}
	@Override
	public String toString() {
		return "Impresa [CodAteco=" + CodAteco + ", Descrizione=" + Descrizione + ", Dim=" + Dim + ", NumImprese="
				+ NumImp + ", TotDip=" + TotDip + ", TotInd=" + TotInd + ", TotExt=" + TotExt + ", TotInt=" + TotInt
				+ "]";
	}

}
