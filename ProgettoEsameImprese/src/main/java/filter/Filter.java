package filter;

import java.util.Collection;

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
