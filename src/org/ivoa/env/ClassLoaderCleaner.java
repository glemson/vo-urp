package org.ivoa.env;

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

  /**
   * Forbidden Constructor
   */
  private ClassLoaderCleaner() {
  }
  
  public static void clean() {
    // hard coded Factory calls to onExit() :
    MetaModelFactory.onExit();
    ModelFactory.onExit();
    
    JAXBFactory.onExit();
    JPAFactory.onExit();
    
    LogUtil.onExit();
  }
}
