package org.ivoa.env;

import org.apache.commons.logging.Log;

import org.ivoa.conf.Configuration;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.LogUtil;

import java.util.Locale;


/**
 * This class contains tools to initialize properly the running Environment  Mainly used by Main classes
 *
 * @author laurent bourges (voparis)
 */
public final class ApplicationAdapter {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  private static final Log log = LogUtil.getLogger();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  private ApplicationAdapter() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   */
  public static void init() {
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.init : enter");
    }

    try {
      // Defines Locale to english (decimal separator 0.x) :
      Locale.setDefault(new Locale("en"));

      // Initialize Configuration :
      final Configuration conf = Configuration.getInstance();

      setSystemProps(conf);

      ApplicationLifeCycle.doStart();
    } catch (final Throwable th) {
      log.error("ApplicationAdapter.init : fatal error : ", th);
    }

    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.init : exit");
    }
  }

  /**
   * TODO : Method Description
   */
  public static void stop() {
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.stop : enter");
    }

    try {
      ApplicationLifeCycle.onExit();
    } catch (final Throwable th) {
      log.error("ApplicationAdapter.stop : fatal error : ", th);
    }

    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.stop : exit");
    }
  }

  /**
   * A first step, defines in code some System.properties to force aatext, mac features ...
   *
   * @param conf Configuration
   */
  private static void setSystemProps(final Configuration conf) {
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.setSystemProps : enter");
    }

    // nothing to do
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.setSystemProps : exit");
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
