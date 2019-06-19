package model;

/** Classe per restituire i metadati (formato JSON) ovvero elenco degli attributi e del tipo della classe {@link Impresa}.
 * @author Lorenzo Iacopini & Lorenzo Fratini
 * @version 1.0
 */
public class Metadati {
	
	//Attributi
	
	private String alias;
	private String sourceField;
	private String type;
	
	//Metodi
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getSourceField() {
		return sourceField;
	}
	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
