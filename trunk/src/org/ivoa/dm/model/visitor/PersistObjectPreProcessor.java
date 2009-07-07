package org.ivoa.dm.model.visitor;

import java.sql.Timestamp;
import java.util.Date;

import org.ivoa.dm.model.MetaDataObjectVisitor;
import org.ivoa.dm.model.MetadataObject;
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
public final class PersistObjectPreProcessor extends MetaDataObjectVisitor {

  /* members : statefull visitor */
  /** user name */
  private final String username;

  /** timestamp = now */
  private final Date now;

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
  public void preProcess(final MetadataObject object) {
      if (object instanceof MetadataRootEntityObject) {
          final MetadataRootEntityObject root = (MetadataRootEntityObject)object;
        root.setModificationDate(now);
        root.setModificationUser(username);
        if (root.isPurelyTransient()) {
          root.setCreationDate(now);
          root.setCreationUser(username);
        }
      }
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------