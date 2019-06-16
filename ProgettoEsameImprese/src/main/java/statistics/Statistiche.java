package statistics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import model.Impresa;


/** Classe per la restituzione delle statistiche.
 * @author Lorenzo Iacopini & Lorenzo Fratini
 *
 */

public class Statistiche implements Operation{
	private String field;
	private int sum;
	private double avg;
	private int max;
	private int min;
	private double std;
	private int count;
	
	
	/**Costruttore che inizializza la variabili d'istanza con dei valori di default.
	 * 
	 */
	public Statistiche() {
		this.field="";
		this.sum=0;
		this.avg=0;
		this.max=0;
		this.min=0;
		this.std=0;
		this.count=0;
	
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public double getStd() {
		return std;
	}
	public void setStd(double std) {
		this.std = std;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public int Somma(int valore,int sum) {
		return sum+valore;
	}
	
	@Override
	public int Max(int valore,Integer massimo) {
		if (valore>massimo) massimo=valore;
		return massimo;
	}
	
	@Override
	public int Min(int valore, Integer minimo) {
		if (valore<minimo) minimo=valore;
		return minimo;
	}
	
	
	//Questo metodo calcola la deviazione standard dei valori in base al campo che si passa nella richiesta
	@Override
	public double std(double avg,Collection<Impresa> dati,String fieldName) {
		double dev_standard=0;
		double diff_al_quadrato=0;
		double somma_diff=0;
		for(Impresa item:dati) {
			try {
				Method m=item.getClass().getMethod("get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1),null);
				try {
					Object tmp = m.invoke(item);
					Integer app=(Integer) tmp;
					diff_al_quadrato=Math.pow(app-avg,2);
					somma_diff+=diff_al_quadrato;
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
		try {
			dev_standard=Math.sqrt(somma_diff)/dati.size();
		} catch(ArithmeticException e) {
			e=new ArithmeticException("ERROR: Radice quadrata di un numero negativo");
			e.printStackTrace();
		}
		return dev_standard;
	}

}


