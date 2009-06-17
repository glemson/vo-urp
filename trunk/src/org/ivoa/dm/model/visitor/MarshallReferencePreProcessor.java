/*
 * IdentityInstantiator.java
 *
 * Author lemson
 * Created on Oct 6, 2008
 */
package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Visitor;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class MarshallReferencePreProcessor extends Visitor {
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
    prepareReferencesForMarshalling(object);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
