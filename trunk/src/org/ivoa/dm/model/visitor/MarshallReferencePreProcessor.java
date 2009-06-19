package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Visitor;

/**
 * MetadataObject Visitor implementation :
 * TODO : description
 *
 * Used by :
 * @see org.ivoa.dm.ModelFactory#marshallObject(String, MetadataObject)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MarshallReferencePreProcessor extends Visitor {

    /** singleton instance (thread safe and stateless) */
    private static MarshallReferencePreProcessor instance = new MarshallReferencePreProcessor();

    /**
     * Return the singleton instance
     * @return visitor
     */
    public static Visitor getInstance() {
        return instance;
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
