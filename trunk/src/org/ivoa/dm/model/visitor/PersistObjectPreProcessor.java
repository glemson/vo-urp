package org.ivoa.dm.model.visitor;

import java.sql.Timestamp;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.Visitor;

/**
 * MetadataObject Visitor implementation :
 * This visitor sets username and update time on MetadattaObjects before peersistence.<br/>
 * Currently only sets this on MetadataRootEntityObjects, as we have these variables only there. This may change in the future.
 *
 * Used by :
 * @see org.ivoa.dm.DataModelManager#persists(List<MetadataRootEntityObject>, String)
 * 
 * @author Gerard Lemson (mpe)
 */
public final class PersistObjectPreProcessor extends Visitor {

    private String username;
    private Timestamp now;

    /**
     * Public constructor. Not protected and not a singleton for we need to preserve state within a persitence transaction.<br/>
     */
    public PersistObjectPreProcessor(String user, Timestamp currentTimestamp) {
        this.username = user;
        this.now = currentTimestamp;
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object MetadataObject instance
     */
    @Override
    public void preProcess(final MetadataObject object) {
        if (object instanceof MetadataRootEntityObject) {
            final MetadataRootEntityObject root = (MetadataRootEntityObject) object;
            root.setDbUpdateTimestamp(now);
            root.setUpdateUser(username);
            if (root.isPurelyTransient()) {
                root.setDBInsertTimestamp(now);
                root.setOwner(username);
            }
        }

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
