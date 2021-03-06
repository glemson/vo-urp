package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObjectVisitor implementation :
 * This updates the objects id-s after persistence.<br/>
 * 
 * Used by :
 * @see org.ivoa.dm.DataModelManager#persist(java.util.List, String)
 *
 * @author Gerard Lemson (mpe)
 */
public final class PersistObjectPostProcessor extends MetaDataObjectVisitor {

  /** singleton instance (java 5 memory model) */
  private static PersistObjectPostProcessor instance = null;

  /**
   * Return the PersistObjectPostProcessor singleton instance
   *
   * @return PersistObjectPostProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final PersistObjectPostProcessor getInstance() {
    if (instance == null) {
      instance = prepareInstance(new PersistObjectPostProcessor());
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
  protected PersistObjectPostProcessor() {
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
    /* nothing to for now.
     * If at some point we store state on objects, here this can be updated
     */
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
