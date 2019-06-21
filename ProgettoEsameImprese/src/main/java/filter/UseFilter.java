package filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;



/** 
 * @author Lorenzo Iacopini & Lorenzo Fratini
 * Classe generica che viene utilizzata per il filtraggio dei dati
 * @param <T> Tipo generico che verrà sostituito con il tipo specifico all'atto della creazione dell'oggetto
 */
public class UseFilter<T> {
	

	/** Metodo che confronta due stringhe oppure due valori numerici rispetto ad un operatore 
	 * @param value Primo valore di confronto
	 * @param operator	Operatore rispetto cui si vogliono confrontare i valori
	 * @param th Secondo valore di confronto
	 * @return	<strong>true</strong> se il confronto va a buon fine <br>
	 * 			<strong>false</strong> se il confronto non va a buon fine
	 * 	
	 */
	public static boolean check(Object value, String operator, Object th) {
		if (th instanceof Number && value instanceof Number) {	
			Integer thC = ((Number)th).intValue();
			Integer valuec = ((Number)value).intValue();
			if (operator.equals("$eq"))
				return value.equals(th);
			else if (operator.equals("$gt"))
				return valuec > thC;
			else if (operator.equals("$lt"))
				return valuec < thC;
			else if(operator.equals("$gte"))
				return valuec>=thC;
			else if(operator.equals("$lte"))
				return valuec<=thC;
		}else if(th instanceof String && value instanceof String)
			return value.equals(th);
		return false;
	}

	
	/**Metodo che scorre l'intera collection passata come parametro ricavando per ogni elemnto il valore cercato.
	 * @param src Collection(ArrayList/HashMap) di cui si vogliono filtrare i dati.
	 * @param fieldName Nome del campo rispetto cui si vuole filtrare.
	 * @param operator Operatore di confronto.
	 * @param value Valore di confronto.
	 * @return Collection <strong>out</strong> contenente gli elementi della Collection <strong>src</strong> passata come 
	 * 			paramentro che soddisfano i criteri di filtraggio.
	 * @throws IllegalAccessException se si vuole ottenere un metodo get di un campo inesistente.
	 * @throws IllegalArgumentException se il nome del campo passato è errato.
	 * @throws NoSuchMethodException se il metodo get ottenuto a seguito del passaggio del campo è inesistente.
	 */
	public Collection<T> select(Collection<T> src, String fieldName, String operator, Object value) {
		Collection<T> out = new ArrayList<T>();
		for(T item:src) {
			try {
				//memorizzo il nome del metodo da lanciare
				Method m = item.getClass().getMethod("get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1),null);
				try {
					//memorizzo il valore del metodo lanciato
					Object tmp = m.invoke(item);
					if(UseFilter.check(tmp, operator, value))
						out.add(item);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
		}
		return out;
	}
	
}
