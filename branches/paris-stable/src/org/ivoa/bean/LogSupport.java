package org.ivoa.bean;

import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * Log Support class to manage LogUtil references and classLoader issues
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class LogSupport {
//~ Constants --------------------------------------------------------------------------------------------------------

  /** 
   * Main Logger for the application
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log log = LogUtil.getLogger();
  /** 
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();
  /** 
   * Logger for development purposes
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logD = LogUtil.getLoggerDev();

//~ End of file --------------------------------------------------------------------------------------------------------
}
