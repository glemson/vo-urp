/*
 * IdentityInstantiator.java
 * 
 * Author lemson
 * Created on Oct 6, 2008
 */
package org.ivoa.dm.model;

import org.ivoa.dm.model.visitor.Visitor;

public class MarshallObjectPostProcessor implements Visitor {

  public void postProcess(MetadataObject object) {
    // TODO Auto-generated method stub

  }

  /**
   * Instantiate the references of the object.<br>
   */
  public void preProcess(MetadataObject object) {
    object.get_state().unsetToBeMarshalled();
    object.getIdentity().setIvoId(null);
    object.getIdentity().setXmlId(null);
  }

}
