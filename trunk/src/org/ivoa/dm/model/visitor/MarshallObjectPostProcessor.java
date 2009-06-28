package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetaDataObjectVisitor;

/**
 * MetadataObject Visitor implementation :
 * TODO : description
 *
 * UNUSED !!
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
  */
public final class MarshallObjectPostProcessor extends MetaDataObjectVisitor {

    /** singleton instance (java 5 memory model) : statically defined (thread safe and stateless) */
    private static MarshallObjectPostProcessor instance = null;

    /**
     * Return the MarshallObjectPostProcessor singleton instance
     *
     * @return MarshallObjectPostProcessor singleton instance
     *
     * @throws IllegalStateException if a problem occured
     */
    public static final MarshallObjectPostProcessor getInstance() {
        if (instance == null) {
            instance = prepareInstance(new MarshallObjectPostProcessor());
        }
        return instance;
    }

    /**
     * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
     * Callback to clean up the possible static references used by this SingletonSupport instance
     * iso clear static references
     *
     * @see org.ivoa.bean.SingletonSupport#clearStaticReferences()
     */
    @Override
    protected void clearStaticReferences() {
        if (instance != null) {
            instance = null;
        }
    }

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected MarshallObjectPostProcessor() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object MetadataObject instance
     */
    @Override
    public void preProcess(final MetadataObject object) {
        getInternalState(object).unsetToBeMarshalled();
        setIvoId(object, null);
        setXmlId(object, null);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
