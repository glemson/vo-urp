package org.ivoa.env;

import org.apache.commons.logging.Log;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ModelFactory;

import org.ivoa.jaxb.JAXBFactory;

import org.ivoa.jpa.JPAFactory;

import org.ivoa.util.LogUtil;


/**
 * Cleans singleton before ClassLoader gets cleared (to ensure good gc)
 *
 * @author laurent bourges (voparis)
 */
public final class ClassLoaderCleaner {
  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden Constructor
   */
  private ClassLoaderCleaner() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   */
  public static void clean() {
    final Log log = LogUtil.getLogger();

    if (log.isWarnEnabled()) {
        log.warn("ClassLoaderCleaner.clean : enter");
    }

    // hard coded Factory calls to onExit() :
    MetaModelFactory.onExit();
    ModelFactory.onExit();

    JAXBFactory.onExit();
    JPAFactory.onExit();

    if (log.isWarnEnabled()) {
        log.warn("ClassLoaderCleaner.clean : exit");
    }

    LogUtil.onExit();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
