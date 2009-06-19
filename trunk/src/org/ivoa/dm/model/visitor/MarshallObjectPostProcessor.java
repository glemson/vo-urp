package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Visitor;

/**
 * MetadataObject Visitor implementation :
 * TODO : description
 *
 * UNUSED !!
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
  */
public final class MarshallObjectPostProcessor extends Visitor {

    /** singleton instance (thread safe and stateless) */
    private static MarshallObjectPostProcessor instance = new MarshallObjectPostProcessor();

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
