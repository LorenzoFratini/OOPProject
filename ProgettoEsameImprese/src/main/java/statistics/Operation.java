package statistics;

import java.util.Collection;

import model.Impresa;


public interface Operation {
	
	
	/** 
	 * @param valore Quantità da aggiungere.
	 * @param sum Valore della somma precedente.
	 * @return <strong>Somma</strong> dei due parametri.
	 */
	abstract int Somma(int valore,int sum);
	/**
	 * @param Valore quantità da confrontare.
	 * @param Massimo quantità con cui fare il confronto.
	 * @return  <strong>Massimo</strong> fra i due parametri.
	 */
	abstract int Max(int valore,int massimo);
	/**
	 * @param Valore quantità da confrontare.
	 * @param Minimo quantità con cui fare il confronto.
	 * @return <strong>Minimo</strong> fra i due parametri.
	 */
	abstract int Min(int valore, int minimo);
	/**
	 * @param avg Media di tutti i valori di un determinato campo di una Collection di {@link Imprese}. 
	 * @param dati  Collection di {@link Impresa} rispetto cui si vuole restituire la statistica.
	 * @param fieldName nome del campo rispetto cui si vogliono ottenere le statistiche
	 * @return <strong>Deviazione Standard</strong> dei tutti i valori.
	 */
	abstract double std(double avg,Collection<Impresa> dati,String fieldName);
	
}
