package filter;

import java.util.Collection;

/**
 * @author Lorenzo Fratini & Lorenzo Iacopini
 *Interfaccia generifica che può essere utilizzata nel momento in cui si richiede un filtraggio dei valori.
 * @param <E> Tipo generico che verrà sostituito con un tipo specifico nel momento in cui il metodo astratto verrà chiamato
 * @param <T> Tipo generico che verrà sostituito con un tipo specifico nel momento in cui il metodo astratto verra chiamato
 */
public interface Filter<E,T> {
	
	
	/**Metodo astratto che verrà implementato nella classe {@ImpresaService} per il filtraggio di una collection secondo specifici parametri passati all richiesta
	 * @param fieldName nome del campo rispetto cui si vuole filtrare.
	 * @param operator operatore secondo cui fare il filtraggio (Conditional:$eq,$gt,$gte;$lt,$lte,$bt
	 *                                                            Loical: $or,$and).
	 * @param value oggetto generico da confrontare per il filtraggio.
	 * @return <strong>Collection<E></strong> Collezione contenente i dati filtrati.
	 */
	abstract Collection<E> filterField(String fieldName, String operator, T value);

}
