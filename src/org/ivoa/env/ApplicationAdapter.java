package org.ivoa.env;

import java.util.Locale;

import org.eclipse.persistence.logging.CommonsLoggingSessionLog;
import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.util.LogUtil;
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
    if (logB.isDebugEnabled()) {
      logB.debug("ApplicationAdapter.start : enter");
    }

    // callback to create resources (Swing application) :

    try {
      // Defines Locale to english (decimal separator 0.x) :
      Locale.setDefault(new Locale("en"));

      // Initialize Configuration :
      Configuration.getInstance();

      ApplicationLifeCycle.doStart();
    } catch (final Throwable th) {
      logB.error("ApplicationAdapter.start : fatal error : ", th);
    }

    // TimerFactory warmup and reset :
    TimerFactory.resetTimers();

    if (logB.isDebugEnabled()) {
      logB.debug("ApplicationAdapter.start : exit");
    }
  }

  /**
   * Stop event
   */
  public static void stop() {
    if (logB.isDebugEnabled()) {
      logB.debug("ApplicationAdapter.stop ...");
    }

    ApplicationLifeCycle.onExit();

    // callback to clean resources :

    try {
      // last one (clear logging) :
      // clean up (GC) : 
      ClassLoaderCleaner.clean();

    } catch (final Throwable th) {
      logB.error("ApplicationAdapter.stop : fatal error : ", th);
    }

    ApplicationLifeCycle.onEnd();

    // TimerFactory dump & exit :
    if (!TimerFactory.isEmpty() && logB.isWarnEnabled()) {
      logB.warn("TimerFactory : statistics : " + TimerFactory.dumpTimers());
    }
    TimerFactory.onExit();

    // LogUtil.onExit must be the last thing to do :
    LogUtil.onExit();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
