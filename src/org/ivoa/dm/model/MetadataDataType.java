package org.ivoa.dm.model;

import org.ivoa.metamodel.DataType;


/**
 * MetadataDataType : Super class for all UML DataType only.
 *
 * @author Gerard Lemson, laurent bourges
 */
public abstract class MetadataDataType extends MetadataElement {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Public No-arg Constructor for JAXB / JPA Compliance
   */
  public MetadataDataType() {
    super();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns DataType metadata via MetaModelFactory singleton
   *
   * @return DataType metadata
   */
  public final DataType getObjectMetaData() {
    return getMetaModelFactory().getDataType(getClassName());
  }

  /**
   * Puts the string representation in the given string buffer :  field name = field value , ...".
   *
   * @param sb given string buffer to fill
   * @param isDeep true means to call toString(sb, true) recursively for all attributes
   *
   * @return the given string buffer filled with the string representation
   */
  @Override
  public StringBuilder toString(final StringBuilder sb, final boolean isDeep) {
    // dump all attributes for Data Types :
    this.deepToString(sb, true, null);

    return sb;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
