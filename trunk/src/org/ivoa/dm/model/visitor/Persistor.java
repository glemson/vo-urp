package org.ivoa.dm.model.visitor;

import javax.persistence.EntityManager;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Visitor;

/**
 * MetadataObject Visitor implementation :
 * This visitor persists incoming objects to the JPA entity manage.<br/>
 *
 * Used by :
 * @see org.ivoa.dm.DataModelManager#persists(java.util.List<MetadataRootEntityObject>, String)
 *
 * @author Gerard Lemson
 */
public final class Persistor extends Visitor {

    private EntityManager em;
    
  /**
     * Public constructor. Not protected and not a singleton for we need to preserve state within a persitence transaction.<br/>
     */
    public Persistor(EntityManager _em) {
        this.em = _em;
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object MetadataObject instance
     */
    @Override
    public void preProcess(final MetadataObject object) {
        em.persist(object);
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
