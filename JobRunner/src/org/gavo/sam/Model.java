package org.gavo.sam;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model {

	public class Parameter
	{
	  public String name;
	  public String description;
	  public Datatype datatype;
	  public int arraysize = 1;
	  public boolean isFixed = false;
	  public Parameter[] group = null;
	  public boolean isGroup()
	  {
		  return group != null;
	  }
	  public ArrayList<String> possibleValues;
	  public String value; // default
	  
	}
	public String name;
	public LinkedHashMap<String,Parameter> parameters;
	
	public Model()
	{
		parameters = new LinkedHashMap<String, Parameter>();
	}
	
}
