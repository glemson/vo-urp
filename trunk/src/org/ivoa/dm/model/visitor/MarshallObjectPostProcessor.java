package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Visitor;

/**
 * MetadataObject Visitor implementation :
 * TODO : description
 *
 * UNUSED !!
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
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
     * TODO : Method Description
     *
     * @param object
     */
    public void postProcess(final MetadataObject object) {
        /* no-op */
    }

    /**
     * Instantiate the references of the object.<br>
     *
     * @param object
     */
    public void preProcess(final MetadataObject object) {
        getInternalState(object).unsetToBeMarshalled();
        setIvoId(object, null);
        setXmlId(object, null);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
