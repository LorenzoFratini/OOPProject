package statistics;

import java.util.Collection;

import model.Impresa;

public interface Operation {
	abstract int Somma(int valore,int sum);
	abstract int Max(int valore,Integer massimo);
	abstract int Min(int valore, Integer minimo);
	abstract double std(double avg,Collection<Impresa> dati,String fieldName);
	
}
