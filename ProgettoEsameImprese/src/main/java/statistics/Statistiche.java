package statistics;

public class Statistiche {
	private String field;
	private int sum;
	private double avg;
	private int max;
	private int min;
	private double std;
	private int count;
	
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
	

}


