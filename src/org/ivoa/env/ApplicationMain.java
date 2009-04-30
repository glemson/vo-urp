package org.ivoa.env;

import org.apache.commons.logging.Log;

import org.ivoa.util.LogUtil;


/**
 * Application Main entry point
 *
 * @author laurent bourges (voparis)
 */
public interface ApplicationMain {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  public static final Log log = LogUtil.getLogger();

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param args 
   */
  public void run(final String[] args);
}
//~ End of file --------------------------------------------------------------------------------------------------------
