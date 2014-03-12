package fr.ensicaen.simulator.model.properties;

import fr.ensicaen.simulator.model.strategies.IStrategy;

public class PropertyDefinition {
	private boolean writable = false;
	private String key;
	private String defaultValue;
	private String comment;
	
	public PropertyDefinition (String _key, String _defaultValue, String _comment, boolean _writable) {
		key = _key;
		defaultValue = _defaultValue;
		comment = _comment;
		writable = _writable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean required) {
		this.writable = required;
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
