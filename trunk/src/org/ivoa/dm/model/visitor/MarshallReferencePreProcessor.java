package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObject TreeVisitor implementation :
 * TODO : description
 *
 * Used by :
 * @see org.ivoa.dm.ModelFactory#marshallObject(String, MetadataObject)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MarshallReferencePreProcessor extends MetaDataObjectVisitor {

  /** singleton instance (java 5 memory model) */
  private static MarshallReferencePreProcessor instance = null;

  /**
   * Return the MarshallReferencePreProcessor singleton instance
   *
   * @return MarshallReferencePreProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final MarshallReferencePreProcessor getInstance() {
    if (instance == null) {
      instance = prepareInstance(new MarshallReferencePreProcessor());
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
  protected MarshallReferencePreProcessor() {
    super();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Process the specified object before its collections are being processed.</br>
   * @param object MetadataObject instance
   */
  @Override
  public void preProcess(final MetadataObject object) {
    prepareReferencesForMarshalling(object);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
