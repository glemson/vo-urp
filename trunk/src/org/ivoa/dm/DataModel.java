package org.ivoa.dm;

/**
 * This class holds all references to manage an UML Data Model : - ModelFactory : marshall / unmarshall XML
 * documents - MetaModelFactory (intermediate model) - JAXBFactory - JPAFactory  Model Version ?  TODO :
 * Implementation & use cases
 *
 * @author laurent bourges (voparis)
 */
public class DataModel {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private final String id;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor for a given UML model identifier
   * 
   * @param id UML identifier
   */
  public DataModel(final String id) {
    this.id = id;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
