package com.hw.autotest.string;

public class Persons {
	private String name=null;
	private String id=null;
	private String value=null;
	private String language = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setNull() {
		this.value = null;
		this.id = null;
		this.name = null;
		this.language = null;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}