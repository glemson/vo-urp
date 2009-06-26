package org.ivoa.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.logging.CommonsLoggingSessionLog;

/**
 * Commons-logging & Log4J Utility class.<br/>
 *
 * This class is the first loaded class (singleton) to provide logging API.<br/>
 * It acts like SingletonSupport but can not use it because SingletonSupport has a direct reference to loggers !
 *
 * @see org.ivoa.bean.SingletonSupport
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class LogUtil {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** internal diagnostic FLAG : use System.out */
  public static final boolean LOGGING_DIAGNOSTICS = false;

  /** internal apache commons Logging diagnostic FLAG : use System.out */
  public static final boolean FORCE_APACHE_COMMONS_LOGGING_DIAGNOSTICS = false;

  /** main logger : main */
  public static final String LOGGER_MAIN = "org.ivoa";

  /** developer logger : dev */
  public static final String LOGGER_DEV = "org.ivoa.dev";

  /** singleton instance (java 5 memory model) */
  private static volatile LogUtil instance = null;

  /** shutdown flag to avoid singleton to be defined (java 5 memory model) */
  private static volatile boolean isShutdown = false;

  static {
    /* static Initializer to call onInit method */
    onInit();
  }

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  // members :
  /** main logger : main */
  private Log log;

  /** developer logger : dev */
  private Log logDev;

  /** all loggers */
  private final Map<String, Log> logs = new HashMap<String, Log>();

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Private Constructor : use getInstance() method
   */
  private LogUtil() {
    /* no-op */
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Returns singleton instance
   * 
   * @return LogUtil singleton instance
   */
  private static LogUtil getInstance() {
    if (instance == null) {
      final LogUtil l = new LogUtil();
      l.init();
      if (isShutdown) {
        // should not be possible :
        if (l.log.isErrorEnabled()) {
          l.log.error("LogUtil.getInstance : shutdown detected : ", new Throwable());
        }
        return l;
      }
      instance = l;

      if (instance.logDev.isInfoEnabled()) {
        instance.logDev.info("LogUtil.getInstance : new singleton : " + instance);
      }
    }

    return instance;
  }

  /**
   * PUBLIC: OnInit method : define system properties for org.apache.commons.logging
   */
  public static final void onInit() {
    if (FORCE_APACHE_COMMONS_LOGGING_DIAGNOSTICS) {
      /**
       * The name (<code>org.apache.commons.logging.diagnostics.dest</code>) of the property used to
       * enable internal commons-logging diagnostic output, in order to get information on what
       * logging implementations are being discovered, what classloaders they are loaded through,
       * etc.
       * <p>
       * If a system property of this name is set then the value is assumed to be the name of a
       * file. The special strings STDOUT or STDERR (case-sensitive) indicate output to System.out
       * and System.err respectively.
       * <p>
       * Diagnostic logging should be used only to debug problematic configurations and should not
       * be set in normal production use.
       */
      System.setProperty("org.apache.commons.logging.diagnostics.dest", "STDOUT");
    }
  }

  /**
   * PUBLIC: onExit method : release all ClassLoader references due to apache commons logging
   * LogFactory NOTE : <b>This method must be called in the context of a web application via
   * ServletContextListener.contextDestroyed(ServletContextEvent)</b>
   * 
   * @see org.apache.commons.logging.LogFactory#release(ClassLoader)
   */
  public static final void onExit() {
    isShutdown = true;
    if (instance != null) {
      if (instance.logDev.isInfoEnabled()) {
        instance.logDev.info("LogUtil.getInstance : free singleton : " + instance);
      }

      // force GC :
      instance.log = null;
      instance.logDev = null;
      instance.logs.clear();

      // free singleton :
      instance = null;

      // Classloader unload problem with commons-logging :
      LogFactory.release(Thread.currentThread().getContextClassLoader());
    }
  }

  /**
   * Returns true if shutdown flag is not set
   * 
   * @return true if shutdown flag is not set
   */
  private static final boolean isRunning() {
    /*
     * LogUtil.isRunning : shutdown detected : java.lang.Throwable at
     * org.ivoa.util.LogUtil.isRunning(LogUtil.java:151) at
     * org.ivoa.util.LogUtil.getLoggerDev(LogUtil.java:177) at
     * org.ivoa.web.servlet.BaseServlet.<clinit>(BaseServlet.java:69) at
     * sun.misc.Unsafe.ensureClassInitialized(Native Method) at
     * sun.reflect.UnsafeFieldAccessorFactory.newFieldAccessor(UnsafeFieldAccessorFactory.java:25)
     * at sun.reflect.ReflectionFactory.newFieldAccessor(ReflectionFactory.java:122) at
     * java.lang.reflect.Field.acquireFieldAccessor(Field.java:918) at
     * java.lang.reflect.Field.getFieldAccessor(Field.java:899) at
     * java.lang.reflect.Field.set(Field.java:657) at
     * org.apache.catalina.loader.WebappClassLoader.clearReferences(WebappClassLoader.java:1644) at
     * org.apache.catalina.loader.WebappClassLoader.stop(WebappClassLoader.java:1524) at
     * org.apache.catalina.loader.WebappLoader.stop(WebappLoader.java:707) at
     * org.apache.catalina.core.StandardContext.stop(StandardContext.java:4557) at
     * org.apache.catalina.manager.ManagerServlet.stop(ManagerServlet.java:1298) at
     * org.apache.catalina.manager.HTMLManagerServlet.stop(HTMLManagerServlet.java:622) at
     * org.apache.catalina.manager.HTMLManagerServlet.doGet(HTMLManagerServlet.java:131)
     */
    if (LOGGING_DIAGNOSTICS && isShutdown) {
      System.out.println("LogUtil.isRunning : shutdown detected : ");
      new Throwable().printStackTrace(System.out);
    }

    return !isShutdown;
  }

  /**
   * Returns main logger : @see LOGGER_MAIN
   * 
   * @return Log instance or null if shutdown flag is set
   */
  public static Log getLogger() {
    Log l = null;
    if (isRunning()) {
      l = getInstance().getLog();
    }
    return l;
  }

  /**
   * Returns the developer logger : @see LOGGER_DEV
   * 
   * @return Log instance or null if shutdown flag is set
   */
  public static Log getLoggerDev() {
    Log l = null;
    if (isRunning()) {
      l = getInstance().getLogDev();
    }
    return l;
  }

  /**
   * Returns a logger for the given key (category)
   * 
   * @param key logger name defined in Log4J.xml
   * @return Log instance or null if shutdown flag is set
   */
  public static Log getLogger(final String key) {
    Log l = null;
    if (isRunning()) {
      l = getInstance().getLog(key);
    }
    return l;
  }

  /**
   * Inits loggers and checks if Log4J is well configured
   * 
   * @throws IllegalStateException if the logger is not a Log4JLogger
   */
  private void init() {
    this.log = this.getLog(LOGGER_MAIN);

    if (!(this.log instanceof org.apache.commons.logging.impl.Log4JLogger)) {
      throw new IllegalStateException(
          "LogUtil : apache Log4J library or log4j.xml file are not present in classpath !");
    }

    // TODO : check if logger has an appender (use parent hierarchy if needed)
    if (this.log.isWarnEnabled()) {
      this.log.warn("LogUtil : logging enabled now.");
    }

    this.logDev = this.getLog(LOGGER_DEV);
  }

  /**
   * Returns a logger for the given key
   * 
   * @param key logger name in log4j.xml
   * @return Log
   * @throws IllegalStateException if the LogFactory returns no logger for the given key
   */
  private Log getLog(final String key) {
    Log l = this.logs.get(key);

    if (l == null) {
      l = LogFactory.getLog(key);

      if (l != null) {
        this.addLog(key, l);
      } else {
        throw new IllegalStateException(
            "LogUtil : Log4J is not initialized correctly : missing logger [" + key
                + "] (check log4j.xml) !");
      }
    }

    return l;
  }

  /**
   * Adds Log into logs map
   * 
   * @param key alias
   * @param logger Log to add
   */
  private void addLog(final String key, final Log logger) {
    this.logs.put(key, logger);
  }

  /**
   * Changes Level for all Loggers to given level
   * 
   * @param level Log4J Level
   */
  private void setLevel(final org.apache.log4j.Level level) {
    if (log.isWarnEnabled()) {
      log.warn("LogUtil : level changed to : " + level.toString());
    }

    for (final Log l : this.logs.values()) {
        setLevel(l, level);
    }
  }

  /**
   * Public method to change the level of a Log4J Logger
   * @param log Log
   * @param level Log4J Level
   */
  public static final void setLevel(final Log log, final org.apache.log4j.Level level) {
    setLevel(getLog4JLogger(log), level);
  }

  /**
   * Public method to change the level of a Log4J Logger
   * @param logger Log4J Logger
   * @param level Log4J Level
   */
  public static final void setLevel(final org.apache.log4j.Logger logger, final org.apache.log4j.Level level) {
    logger.setLevel(level);

    // Reset the cachedLevel for the log hierarchy :
    CommonsLoggingSessionLog.resetCachedLevels();
  }

  /**
   * Returns Log4JLogger
   * 
   * @param l Log
   * @return Log4JLogger
   */
  private static final org.apache.log4j.Logger getLog4JLogger(final Log l) {
    return ((org.apache.commons.logging.impl.Log4JLogger) l).getLogger();
  }

  /**
   * Returns main logger
   * 
   * @return Log
   */
  private final Log getLog() {
    return this.log;
  }

  /**
   * Returns developer logger
   * 
   * @return Log
   */
  private final Log getLogDev() {
    return this.logDev;
  }

  // public methods :
  /**
   * Changes Level for all Loggers to DEBUG Level
   */
  public final void setDebug() {
    setLevel(org.apache.log4j.Level.DEBUG);
  }

  /**
   * Changes Level for all Loggers to WARNING Level
   */
  public final void setWarn() {
    setLevel(org.apache.log4j.Level.WARN);
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
