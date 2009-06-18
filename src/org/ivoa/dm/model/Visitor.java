package org.ivoa.dm.model;

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
 * @author Gerard Lemson
 * @since 29 Jun 200829 Jun 2008
 */
public abstract class Visitor {

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected Visitor() {
        /* no-op */
    }
    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object MetadataObject instance
     */
    public abstract void preProcess(final MetadataObject object);

    /**
     * Process the specified object after its collections have been processed.</br>
     * @param object MetadataObject instance
     */
    public abstract void postProcess(final MetadataObject object);


    /*
     * Utility methods for accessing protected methods of MetadataObject
     * 
     * Facade like pattern
     */
    
    /**
     * return the status object of the metadata object
     *
     * @return state instance
     */
    protected final State getInternalState(final MetadataObject object) {
        return object.getInternalState();
    }

    /**
     * Sets the external identifier (URI)
     *
     * @param pIvoId external identifier (URI)
     */
    protected final void setIvoId(final MetadataObject object, final String pIvoId) {
        object.getIdentity().setIvoId(pIvoId);
    }

    /**
     * Sets the local xsd:ID for this object
     *
     * @param pXmlId local xsd:ID for this object
     */
    protected final void setXmlId(final MetadataObject object, final String pXmlId) {
        object.getIdentity().setXmlId(pXmlId);
    }

    protected void resetReferencesAfterMarshalling(MetadataObject object) {
        object.resetReferencesAfterMarshalling();
    }

    protected void prepareReferencesForMarshalling(MetadataObject object) {
        object.prepareReferencesForMarshalling();
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------





