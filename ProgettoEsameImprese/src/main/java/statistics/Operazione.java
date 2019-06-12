package statistics;

public class Operazione {
	public int Somma(int valore,Integer sum) {
		return sum.intValue()+valore;
	}
		
	
	public int Count(int conteggio) {
		return conteggio++;
	}
	
	public int Max(int valore,Integer massimo) {
		if (valore>massimo) massimo=valore;
		return massimo;
	}
	
	
	public int Min(int valore, Integer minimo) {
		if (valore<minimo) minimo=valore;
		return minimo;
	}
	
	
	//Questo metodo calcola la deviazione standard dei valori in base al campo che si passa nella richiesta
	public double Diff_al_quadrato(double avg,int valore) {
		return Math.pow(valore-avg, 2);
	}
	


}
