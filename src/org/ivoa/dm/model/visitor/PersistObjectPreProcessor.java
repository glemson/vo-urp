package org.ivoa.dm.model.visitor;

import java.sql.Timestamp;

import org.ivoa.dm.model.MetaDataObjectVisitor;
import org.ivoa.dm.model.MetadataRootEntityObject;

/**
 * MetadataObject Visitor implementation : This visitor sets username and update time on
 * MetadattaObjects before peersistence.<br/>
 * Currently only sets this on MetadataRootEntityObjects, as we have these variables only there.
 * This may change in the future. Used by :
 * 
 * @see org.ivoa.dm.DataModelManager#persist(java.util.List, String)
 * @author Gerard Lemson (mpe)
 */
public final class PersistObjectPreProcessor extends MetaDataObjectVisitor<MetadataRootEntityObject> {

  /* members : statefull visitor */
  /** user name */
  private final String username;

  /** timestamp = now */
  private final Timestamp now;

  /**
   * Public constructor.<br/>
   * Not protected and not a singleton for we need to preserve state within a persistence
   * transaction.
   * 
   * @param user user name
   * @param currentTimestamp now
   */
  public PersistObjectPreProcessor(final String user, final Timestamp currentTimestamp) {
    username = user;
    now = currentTimestamp;
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Process the specified object before its collections are being processed.</br>
   * 
   * @param object MetadataObject instance
   */
  @Override
  public void preProcess(final MetadataRootEntityObject object) {
    object.setDbUpdateTimestamp(now);
    object.setUpdateUser(username);
    if (object.isPurelyTransient()) {
      object.setDBInsertTimestamp(now);
      object.setOwner(username);
    }
  }

  /**
   * Process the specified object after its collections have been processed.</br>
   * 
   * @param object MetadataObject instance
   */
  @Override
  public void postProcess(final MetadataRootEntityObject object) {
    /* no-op */
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
