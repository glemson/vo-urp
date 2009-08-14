package org.ivoa.runner;


import org.ivoa.service.*;
import javax.servlet.ServletContext;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.env.ClassLoaderCleaner;
import org.ivoa.util.LogUtil;
import org.ivoa.util.timer.TimerFactory;


/**
 * Facade pattern for the web application
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class JobRunnerFacade extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  public static final String CTX_KEY = "VO_URP_Facade";
  /** TODO : Field Description */
  public static final int DEF_PAGE_SIZE = 10;
  /** TODO : Field Description */
  private static JobRunnerFacade instance = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  private JobRunnerFacade() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final JobRunnerFacade getInstance() {
    return instance;
  }

  /**
   * Creates the facade :
   * Used by ApplicationManager
   *
   * @param ctx application context
   */
  public static void createInstance(final ServletContext ctx) {
    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is starting ...");
    }

    final JobRunnerFacade facade = new JobRunnerFacade();

    // defines the singleton before initialization :
    // check if really necessary :
    instance = facade;

    facade.prepare();

    // finally add this component to the application context :
    ctx.setAttribute(CTX_KEY, instance);
  }

  /**
   * Release the facade :
   * Used by ApplicationManager
   *
   * @param ctx application context
   */
  public static void freeInstance(final ServletContext ctx) {
    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is stopping ...");
    }

    // first remove this component from the application context :
    ctx.removeAttribute(CTX_KEY);

    if (instance != null) {
      instance.exit();
    }

    // force GC :
    instance = null;

    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is unavailable.");
    }

    try {
      // last one (clear logging) :
      // clean up (GC) :
      ClassLoaderCleaner.clean();

    } catch (final Throwable th) {
      log.error("VO_URP_Facade.freeInstance : fatal error : ", th);
    }

    // TimerFactory dump & exit :
    if (!TimerFactory.isEmpty() && logB.isWarnEnabled()) {
      logB.warn("TimerFactory : statistics : " + TimerFactory.dumpTimers());
    }
    TimerFactory.onExit();

    // LogUtil.onExit must be the last thing to do :
    LogUtil.onExit();
  }

  /**
   * TODO : Method Description
   */
  private void prepare() {
    if (log.isInfoEnabled()) {
      log.info("loading configuration ...");
    }

    Configuration.getInstance();

    // TimerFactory warmup and reset :
    TimerFactory.resetTimers();

    // Enable the session monitor :
    SessionMonitor.onInit();

    // Starts the job engine :
    JobRunnerApplication.onInit();

    if (log != null) {
      log.warn("Application is ready.");
    }
  }

  // Exit :
  /**
   * TODO : Method Description
   */
  public void exit() {
    if (log.isInfoEnabled()) {
      log.info("VO_URP_Facade.exit : enter");
    }
    
    // Starts the job engine :
    JobRunnerApplication.onExit();

    // free Session stats Thread :
    SessionMonitor.onExit();

    if (log.isInfoEnabled()) {
      log.info("VO_URP_Facade.exit : exit");
    }
  }

}
//~ End of file --------------------------------------------------------------------------------------------------------

