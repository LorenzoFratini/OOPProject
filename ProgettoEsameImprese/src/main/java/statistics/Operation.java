package statistics;

import java.util.Collection;

import model.Impresa;


public interface Operation {
	
	
	/** 
	 * @param valore quantità da aggiungere.
	 * @param sum valore della somma precedente.
	 * @return somma dei due valori.
	 */
	abstract int Somma(int valore,int sum);
	/**
	 * @param valore quantità da confrontare.
	 * @param massimo quantità con cui fare il confronto.
	 * @return il massimo fra i due valori.
	 */
	abstract int Max(int valore,Integer massimo);
	/**
	 * @param valore quantità da confrontare.
	 * @param minimo quantutà con cui fare il confronto.
	 * @return  il minimo fra i due valori.
	 */
	abstract int Min(int valore, Integer minimo);
	/**
	 * @param avg media di tutti i valori di un determinato campo di una certa Collection di {@Imprese}. 
	 * @param dati  Collection di {@Impresa} rispetto a cui si vuole restituire la statistica.
	 * @param fieldName nome del campo su cui si vuole effettuare la statistica
	 * @return deviziazione standard.
	 */
	abstract double std(double avg,Collection<Impresa> dati,String fieldName);
	
}
