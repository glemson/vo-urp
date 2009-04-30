package org.ivoa.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Commons-logging & Log4J Utility class :
 *
 * @author laurent bourges (voparis)
 */
public final class LogUtil {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** main logger : main */
  public static final String LOGGER_MAIN = "org.ivoa";
  /** developper logger : dev */
  public static final String LOGGER_DEV = "org.ivoa.dev";
  /** singleton instance */
  private static volatile LogUtil instance = null;

  //~ Members ----------------------------------------------------------------------------------------------------------

  // members :
  /** main logger : main */
  private Log log;
  /** developper logger : dev */
  private Log logDev;
  /** all loggers */
  private final Map<String, Log> logs = new HashMap<String, Log>();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Private Constructor : use getInstance() method
   */
  private LogUtil() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns singleton instance
   *
   * @return LogUtil singleton instance
   */
  public static LogUtil getInstance() {
    if (instance == null) {
      final LogUtil l = new LogUtil();

      l.init();
      instance = l;
    }

    return instance;
  }

  /**
   * Called on exit (clean up code)
   */
  public static final void onExit() {
    if (instance != null) {
      // Classloader unload problem with commons-logging :
      LogFactory.release(Thread.currentThread().getContextClassLoader());

      // clean up :
      instance = null;
    }
  }

  /**
   * Returns main logger
   *
   * @return Log
   */
  public static Log getLogger() {
    return getInstance().getLog();
  }

  /**
   * Returns developper logger
   *
   * @return Log
   */
  public static Log getLoggerDev() {
    return getInstance().getLogDev();
  }

  /**
   * Returns logger by key
   *
   * @param key logger name in Log4J.xml
   *
   * @return Log
   */
  public static Log getLogger(final String key) {
    return getInstance().getLog(key);
  }

  /**
   * Inits loggers and checks if Log4J is well configured
   *
   * @throws IllegalStateException
   */
  private void init() {
    this.log = getLog(LOGGER_MAIN);

    if (! (this.log instanceof org.apache.commons.logging.impl.Log4JLogger)) {
      throw new IllegalStateException(
        "LogUtil : apache Log4J library or log4j.xml file are not present in classpath !");
    }

    /*    else {
       if (!org.apache.log4j.Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
         throw new IllegalStateException("LogUtil : Log4J is not initialized correctly : missing root appender (check log4j.xml) !");
       }
       }
     */

    // TODO : check if logger has an appender (use parent hierarchy if needed)
    if (this.log.isWarnEnabled()) {
      this.log.warn("LogUtil : logging enabled now.");
    }

    this.logDev = getLog(LOGGER_DEV);
  }

  /**
   * Returns main logger
   *
   * @param key logger name in log4j.xml
   *
   * @return Log
   *
   * @throws IllegalStateException
   */
  private Log getLog(final String key) {
    Log l = this.logs.get(key);

    if (l == null) {
      l = LogFactory.getLog(key);

      if (l != null) {
        addLog(key, l);
      } else {
        throw new IllegalStateException(
          "LogUtil : Log4J is not initialized correctly : missing logger [" + key + "] (check log4j.xml) !");
      }
    }

    return l;
  }

  /**
   * Adds Log into logs map
   *
   * @param key alias
   * @param log Log
   */
  private void addLog(final String key, final Log log) {
    this.logs.put(key, log);
  }

  /**
   * Changes Level for all Loggers to given level
   *
   * @param level Log4J Level
   */
  private void setLevel(final org.apache.log4j.Level level) {
    for (final Log l : this.logs.values()) {
      getLog4JLogger(l).setLevel(level);
    }
  }

  /**
   * Returns Log4JLogger
   *
   * @param l Log
   *
   * @return Log4JLogger
   */
  private org.apache.log4j.Logger getLog4JLogger(final Log l) {
    return ((org.apache.commons.logging.impl.Log4JLogger) l).getLogger();
  }

  /**
   * Returns main logger
   *
   * @return Log
   */
  private Log getLog() {
    return this.log;
  }

  /**
   * Returns developper logger
   *
   * @return Log
   */
  private Log getLogDev() {
    return this.logDev;
  }

  // public methods :
  /**
   * Changes Level for all Loggers to DEBUG Level
   */
  public void setDebug() {
    setLevel(org.apache.log4j.Level.DEBUG);
    this.log.error("LogUtil : level changed to : DEBUG");
  }

  /**
   * Changes Level for all Loggers to WARNING Level
   */
  public void setWarn() {
    setLevel(org.apache.log4j.Level.WARN);
    this.log.error("LogUtil : level changed to : WARN");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
