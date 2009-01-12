/*
 * Concept.java
 * 
 * Author Gerard Lemson
 * Created on 11 May 2008
 */
package org.ivoa.dm.semantics;

public class Concept {
  /** The ontology this concept belongs to */
  private Ontology ontology;
  /** The name by which this concept is known in its ontology. Name is unique in its ontology. */
  private String name;
  /** The globally unique URI by which this concept is known in it's ontology */
  private String URI;
  /** An English language description of this concept */
  private String description_en;
  /** Protected constructor, concepts cna only be created by the OntologyFactory. */
  protected Concept(Ontology _ontology)
  {
    if (_ontology == null)
      throw new IllegalArgumentException("A concept must be created with its ontology.");
    this.ontology = _ontology;
  }
}
