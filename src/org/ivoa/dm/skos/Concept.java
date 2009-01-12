/*
 * Concept.java
 * 
 * Author lemson
 * Created on Dec 16, 2008
 */
package org.ivoa.dm.skos;

public class Concept {

  public String name;
  public String description;
  
  public String toString()
  {
    return "["+name +" : "+description+"]";
  }
  
}
