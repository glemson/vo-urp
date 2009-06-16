package org.ivoa.env;


import java.util.Locale;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;


/**
 * This class defines a way to initialize properly the running Environment and indicate that an
 * implementation interacts with the application life cycle Mainly used by Main classes
 *
 * @author laurent bourges (voparis)
 */
public final class ApplicationAdapter extends LogSupport {

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  private ApplicationAdapter() {
    /* no-op */
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
      Configuration.getInstance();

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
}
//~ End of file --------------------------------------------------------------------------------------------------------
