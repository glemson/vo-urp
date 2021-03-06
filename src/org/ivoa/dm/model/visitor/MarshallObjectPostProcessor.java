package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObjectVisitor implementation :
 * TODO : description
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MarshallObjectPostProcessor extends MetaDataObjectVisitor {

  /** singleton instance (java 5 memory model) */
  private static MarshallObjectPostProcessor instance = null;

  /**
   * Return the MarshallObjectPostProcessor singleton instance
   *
   * @return MarshallObjectPostProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final MarshallObjectPostProcessor getInstance() {
    if (instance == null) {
      instance = prepareInstance(new MarshallObjectPostProcessor());
    }
    return instance;
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance
   * iso clear static references
   *
   * @see org.ivoa.bean.SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    if (instance != null) {
      instance = null;
    }
  }

  /**
   * Protected constructor to avoid to create instance except for singletons (stateless classes)
   */
  protected MarshallObjectPostProcessor() {
    super(true);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Process the specified object
   *
   * @param object MetadataObject instance
   * @param argument optional argument
   */
  @Override
  public void process(final MetadataObject object, final Object argument) {
    getInternalState(object).unsetToBeMarshalled();
    setIvoId(object, null);
    setXmlId(object, null);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
