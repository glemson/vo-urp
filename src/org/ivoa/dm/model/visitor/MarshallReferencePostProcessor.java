package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObject Visitor implementation :
 * TODO : description
 *
 * Used by :
 * @see org.ivoa.dm.ModelFactory#marshallObject(String, MetadataObject)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MarshallReferencePostProcessor extends MetaDataObjectVisitor {

    /** singleton instance (java 5 memory model) : statically defined (thread safe and stateless) */
    private static MarshallReferencePostProcessor instance = new MarshallReferencePostProcessor();

    /**
     * Return the singleton instance
     * @return visitor
     */
    public static MarshallReferencePostProcessor getInstance() {
        return instance;
    }

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected MarshallReferencePostProcessor() {
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
        resetReferencesAfterMarshalling(object);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
