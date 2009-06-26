package org.ivoa.dm.model;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.dm.model.visitor.Visitor;

/**
 * Represents a visitor in the visitor pattern.<br>
 * Can traverse a MetadataObjbect containment tree.
 *
 * By default traverses only collections.
 * Has a pre- and a postprocess method.
 * The former is called before the contained collections are traversed,
 * the latter afterwards.
 *
 * TODO decide whether we want to traverse a complete graph, in which case
 * we need to keep track of whether objects have already been visited, as
 * objects may now be reached in multiple ways.
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class MetaDataObjectVisitor extends SingletonSupport implements Visitor<MetadataObject> {

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected MetaDataObjectVisitor() {
        /* no-op */
    }
    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object after its collections have been processed.</br>
     * @param object MetadataObject instance
     */
    public void postProcess(final MetadataObject object) {
        /* no-op */
    }

    /*
     * Utility methods for accessing protected methods of MetadataObject
     * 
     * Facade like pattern
     */
    /**
     * Return the status object of the MetadataObject instance
     * 
     * @param object MetadataObject instance
     * @return state instance
     */
    protected final State getInternalState(final MetadataObject object) {
        return object.getInternalState();
    }

    /**
     * Set the external identifier (URI) on the MetadataObject instance
     *
     * @param object MetadataObject instance
     * @param pIvoId external identifier (URI)
     */
    protected final void setIvoId(final MetadataObject object, final String pIvoId) {
        object.getIdentity().setIvoId(pIvoId);
    }

    /**
     * Set the local xsd:ID for this object on the MetadataObject instance
     *
     * @param object MetadataObject instance
     * @param pXmlId local xsd:ID for this object
     */
    protected final void setXmlId(final MetadataObject object, final String pXmlId) {
        object.getIdentity().setXmlId(pXmlId);
    }

    /**
     * Set all Reference objects to null on the MetadataObject instance
     * 
     * @param object MetadataObject instance
     */
    protected final void resetReferencesAfterMarshalling(final MetadataObject object) {
        object.resetReferencesAfterMarshalling();
    }

    /**
     * Set all Reference objects to their appropriate value on the MetadataObject instance
     * 
     * @param object MetadataObject instance
     */
    protected final void prepareReferencesForMarshalling(final MetadataObject object) {
        object.prepareReferencesForMarshalling();
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
