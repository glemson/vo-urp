package org.ivoa.web.servlet;

import org.ivoa.service.VO_URP_Facade;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;


/**
 * JPARequestListener
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class JPARequestListener implements ServletRequestListener {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param evt 
   */
  public void requestInitialized(final ServletRequestEvent evt) {
    /* no-op */
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
