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
    final Log logD = LogUtil.getLoggerDev();

    if (logD.isInfoEnabled()) {
      logD.info("ClassLoaderCleaner.clean : enter");
    }

    /* release Singleton resources */
    SingletonSupport.onExit();

    // ThreadLocal checks :
    ThreadLocalCleaner.checkThreads();

    if (logD.isInfoEnabled()) {
      logD.info("ClassLoaderCleaner.clean : exit");
    }

    LogUtil.onExit();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
