package org.ivoa.env;

import org.apache.commons.logging.Log;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.LogUtil;

/**
 * Cleans singleton before ClassLoader gets cleared (to ensure good gc)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ClassLoaderCleaner {
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Forbidden Constructor
   */
  private ClassLoaderCleaner() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * clean up resource located in the classLoader like factories, singletons ...
   */
  public static void clean() {
    /*
     * Logger for the base framework
     * @see org.ivoa.bean.LogSupport
     */
    final Log logB = LogUtil.getLoggerBase();

    if (logB.isWarnEnabled()) {
      logB.warn("ClassLoaderCleaner.clean : enter");
    }

    /* release Singleton resources */
    SingletonSupport.onExit();

    // ThreadLocal checks :
    ThreadLocalCleaner.cleanAndcheckThreads();

    if (logB.isWarnEnabled()) {
      logB.warn("ClassLoaderCleaner.clean : exit");
    }

    LogUtil.onExit();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
