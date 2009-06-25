package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObject Visitor implementation :
 * This updates the objects id-s after persistence.<br/>
 * 
 * Used by :
 * @see org.ivoa.dm.DataModelManager#persist(java.util.List, String)
 *
 * @author Gerard Lemson (mpe)
  */
public final class PersistObjectPostProcessor extends MetaDataObjectVisitor {

    /** singleton instance (java 5 memory model) : statically defined (thread safe and stateless) */
  private static PersistObjectPostProcessor instance = new PersistObjectPostProcessor();

  /**
   * Return the singleton instance
   * @return visitor
   */
  public static PersistObjectPostProcessor getInstance() {
      return instance;
  }

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected PersistObjectPostProcessor() {
        super();

        // register this instance in SingletonSupport
        register(this);
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object MetadataObject instance
     */
    @Override
    public void preProcess(final MetadataObject object) {
      /* no-op for now 
       * If at some point we store state on objects, here this can be updated
       * */
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
