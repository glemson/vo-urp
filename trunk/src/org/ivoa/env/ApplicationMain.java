package org.ivoa.env;

import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * Application Main entry point
 *
 * @author laurent bourges (voparis)
 */
public interface ApplicationMain {

  /** logger */
  public final static Log log = LogUtil.getLogger();

  public void run(final String[] args);
  
}
