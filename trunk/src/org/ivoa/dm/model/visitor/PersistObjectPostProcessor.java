package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.Visitor;
import java.sql.Timestamp;

/**
 * MetadataObject Visitor implementation :
 * This updates the objects id-s after persistence.<br/>
 * 
 * Used by :
 * @see org.ivoa.dm.DataModelManager#persists(List<MetedataRootEntityObject, String)
 *
 * @author Gerard Lemson (mpe)
  */
public final class PersistObjectPostProcessor extends Visitor {

  /** singleton instance (thread safe and stateless) */
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

    /**
     * Process the specified object after its collections have been processed.</br>
     * @param object MetadataObject instance
     */
    @Override
    public void postProcess(final MetadataObject object) {
        /* no-op */
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
