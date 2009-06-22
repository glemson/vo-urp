package org.ivoa.env;

import org.apache.commons.logging.Log;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ModelFactory;
import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.jpa.JPAFactory;
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

    // hard coded Factory calls to onExit() :
    ModelFactory.onExit();

    JAXBFactory.onExit();
    JPAFactory.onExit();

    /*
     * TODO : Use the SingletonSupport pattern to know which class should be cleaned up :
     */

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
