/*
 * IdentityInstantiator.java
 *
 * Author lemson
 * Created on Oct 6, 2008
 */
package org.ivoa.dm.model;

import org.ivoa.dm.model.visitor.Visitor;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class MarshallReferencePostProcessor implements Visitor {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param object 
   */
  public void postProcess(final MetadataObject object) {
    // TODO Auto-generated method stub
  }

  /**
   * Instantiate the references of the object.<br>
   *
   * @param object
   */
  public void preProcess(final MetadataObject object) {
    object.resetReferencesAfterMarshalling();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
