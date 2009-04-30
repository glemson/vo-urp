/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivoa.dm.model;

import java.util.Map;


/**
 * 
DOCUMENT ME!
 *
 * @author laurent
 */
public class RootEntityObject extends MetadataObject {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param name 
   */
  public void setOwner(final String name) {
  }

  /**
   * TODO : Method Description
   *
   * @param name 
   */
  public void setUpdateUser(final String name) {
  }

  /**
   * TODO : Method Description
   *
   * @param sb 
   * @param isDeep 
   * @param ids 
   *
   * @return value TODO : Value Description
   */
  protected StringBuilder deepToString(final StringBuilder sb, final boolean isDeep,
                                       final Map<MetadataElement, Object> ids) {
    return null;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
