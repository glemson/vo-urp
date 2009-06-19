package org.ivoa.env;


import java.util.Locale;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.util.timer.TimerFactory;


/**
 * This class defines a way to initialize properly the running Environment and indicate that an
 * implementation interacts with the application life cycle Mainly used by Main classes
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
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
   * Start event
   */
  public static void start() {
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.start : enter");
    }

    // callback to create resources (Swing application) :

    try {
      // Defines Locale to english (decimal separator 0.x) :
      Locale.setDefault(new Locale("en"));

      // Initialize Configuration :
      Configuration.getInstance();

      ApplicationLifeCycle.doStart();
    } catch (final Throwable th) {
      log.error("ApplicationAdapter.start : fatal error : ", th);
    }

    // TimerFactory warmup and reset :
    TimerFactory.resetTimers();
    
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.start : exit");
    }
  }

  /**
   * Stop event
   */
  public static void stop() {
    if (log.isDebugEnabled()) {
      log.debug("ApplicationAdapter.stop ...");
    }
    
    ApplicationLifeCycle.onExit();

    // callback to clean resources :

    // TimerFactory dump :
    if (logD.isWarnEnabled()) {
      logD.warn("TimerFactory : statistics : " + TimerFactory.dumpTimers());
    }
    
    try {
      // last one (clear logging) :
      // clean up (GC) : 
      ClassLoaderCleaner.clean();
      
    } catch (final Throwable th) {
      log.error("ApplicationAdapter.stop : fatal error : ", th);
    }

    ApplicationLifeCycle.onEnd();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
