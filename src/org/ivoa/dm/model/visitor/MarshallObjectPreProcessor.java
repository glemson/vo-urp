package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObjectVisitor implementation :
 * TODO : description
 *
 * Used by :
 * @see org.ivoa.dm.ModelFactory#marshallObject(String, MetadataObject)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MarshallObjectPreProcessor extends MetaDataObjectVisitor {

  /** singleton instance (java 5 memory model) */
  private static MarshallObjectPreProcessor instance = null;

  /**
   * Return the MarshallObjectPreProcessor singleton instance
   *
   * @return MarshallObjectPreProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final MarshallObjectPreProcessor getInstance() {
    if (instance == null) {
      instance = prepareInstance(new MarshallObjectPreProcessor());
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
  protected MarshallObjectPreProcessor() {
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
    getInternalState(object).setToBeMarshalled();
    if (object.getId() != null && object.getId().longValue() >= 0l) {
      // do not set ivoId if object is purely transient.
      setIvoId(object, object.getIvoId());
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
