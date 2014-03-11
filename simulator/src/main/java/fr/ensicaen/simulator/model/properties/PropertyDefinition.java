package fr.ensicaen.simulator.model.properties;

public class PropertyDefinition {
	private String key;
	private String defaultValue;
	private boolean required = false;
	private String comment;

	public PropertyDefinition(String key, String defaultValue, boolean required, String comment) {
		super();
		this.required = required;
		this.key = key;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String value) {
		this.defaultValue = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
