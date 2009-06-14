package org.ivoa.web.servlet;

import org.ivoa.service.VO_URP_Facade;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;


/**
 * JPARequestListener.java
 *
 * Created on 12 sept. 2007, 15:44:15
 *
 * @author laurent
 */
public class JPARequestListener implements ServletRequestListener {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param evt 
   */
  public void requestInitialized(final ServletRequestEvent evt) {
  }

  /**
   * TODO : Method Description
   *
   * @param evt 
   */
  public void requestDestroyed(final ServletRequestEvent evt) {
    final VO_URP_Facade facade = VO_URP_Facade.getInstance();

    if (facade != null) {
      facade.closeEntityManager();
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
