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
public final class MarshallObjectPreProcessor extends MetaDataObjectVisitor {

    /** singleton instance (java 5 memory model) : statically defined (thread safe and stateless) */
    private static MarshallObjectPreProcessor instance = new MarshallObjectPreProcessor();

    /**
     * Return the singleton instance
     * @return visitor
     */
    public static MetaDataObjectVisitor getInstance() {
        return instance;
    }

    /**
     * Protected constructor to avoid to create instance except for singletons (stateless classes)
     */
    protected MarshallObjectPreProcessor() {
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
      getInternalState(object).setToBeMarshalled();
      setIvoId(object, object.getIvoId());
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
