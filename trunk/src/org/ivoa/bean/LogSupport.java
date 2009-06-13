package org.ivoa.bean;

import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * Log Support class to manage LogUtil references and classLoader issues
 *
 * @author laurent bourges
 */
public class LogSupport {
//~ Constants --------------------------------------------------------------------------------------------------------

  /** Logger for this class and subclasses */
  protected static Log log = LogUtil.getLogger();
  /** Dev Logger for this class and subclasses */
  protected static Log logD = LogUtil.getLoggerDev();

//~ End of file --------------------------------------------------------------------------------------------------------
}
