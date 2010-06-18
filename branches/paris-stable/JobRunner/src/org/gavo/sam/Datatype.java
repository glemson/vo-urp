package org.gavo.sam;

public enum Datatype {

	_string("string"),
	_int("int"), 
	_float("float"),
	_double("double"),
	_long("long"),
	_boolean("boolean"),
	_datetime("datetime"),
	_short("short"),
	_file("file");
	
	private String title;
	private Datatype(String title)
	{
		this.title= title;
	}
	public String getTitle() {
		return title;
	}
}
