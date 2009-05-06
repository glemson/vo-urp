package org.eclipse.persistence.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.impl.Log4JLogger;

import org.apache.log4j.Logger;

import org.eclipse.persistence.sessions.Session;

import java.io.OutputStream;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


/**
 * PUBLIC: This is a wrapper class for org.apache.commons.logging.Log.   It is used when messages need to be logged
 * through apache commons logging 1.1.   History : 05/05/2009 : Updated API to EclipseLink 1.1 version + Javadoc fixes
 * TODO : Use Enum instead of int values for Levels (OO Design)
 *
 * @author laurent bourges (voparis)
 *
 * @see SessionLog
 * @see AbstractSessionLog
 * @see SessionLogEntry
 * @see Session
 */
public final class CommonsLoggingSessionLog extends AbstractSessionLog {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** internal debugger FLAG : use System.out */
  private static final boolean FORCE_INTERNAL_DEBUG = false;
  /** internal cache FLAG for LogWrapper's levels */
  private static final boolean USE_INTERNAL_CACHE = true;
  /** value corresponding to an undefined Level */
  public static final int UNDEFINED_LEVEL = -1;
  /**
   * Stores the default session name in case there is the session name is missing. org.eclipse.persistence
   * package used by Log4J configuration
   */
  public static final String ECLIPSELINK_NAMESPACE = "org.eclipse.persistence";
  /** org.eclipse.persistence.default used by Log4J configuration */
  public static final String DEFAULT_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + ".default";
  /** org.eclipse.persistence.session used by Log4J configuration */
  public static final String SESSION_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + ".session";
  /** Copied from JavaLog for compatibility issues */
  public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
  /** Copied from JavaLog for compatibility issues */
  public static final String LOGGING_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource";
  /** Copied from JavaLog for compatibility issues */
  public static final String TRACE_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource";
  /** Stores all the java.util.logging.Levels.  The indexes are TopLink logging levels. */
  public static final Level[] JAVA_LEVELS = new Level[] {
                                              Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO,
                                              Level.WARNING, Level.SEVERE, Level.OFF
                                            };

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** formats the EclipseLinkLogRecords. Acts as a static variable but not declared static to avoid classLoader leaks */
  private final LogFormatter LOG_FORMATTER = new LogFormatter();
  /**
   * Represents the HashMap that stores all the name space strings. The keys are category names.  The values are
   * namespace strings. Acts as a static variable but not declared static to avoid classLoader leaks
   */
  private final Map<String, String> NAMESPACE_MAP = new ConcurrentHashMap<String, String>();
  /** LogWrapper instances. Acts as a static variable but not declared static to avoid classLoader leaks */
  private final Map<String, LogWrapper> CATEGORY_LOGGERS = new ConcurrentHashMap<String, LogWrapper>();
  /** Stores the namespace for session, i.e."org.eclipse.persistence.session.#sessionname#". */
  private String sessionNameSpace;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
     * PUBLIC:
     *
     * CommonsLoggingSessionLog Constructor.
     * This adds a root logger for DEFAULT_ECLIPSELINK_NAMESPACE.
     */
  public CommonsLoggingSessionLog() {
    super();

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.new : instance : " + this);
    }

    addLogger(DEFAULT_ECLIPSELINK_NAMESPACE, DEFAULT_ECLIPSELINK_NAMESPACE);

    //        setShouldPrintSession(false);
    // date is always given by log4J :
    setShouldPrintDate(false);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * PUBLIC:  Return the effective log level for the name space extracted from session and category. If a
   * Logger's level is set to be null then the Logger will use an effective Level that will be obtained by walking up
   * the parent tree and using the first non-null Level.
   *
   * @param category category
   *
   * @return the effective level according to the java.util.logging.Levels
   */
  @Override
  public final int getLevel(final String category) {
    final LogWrapper lw = getLogWrapper(category);

    int              l = OFF;

    if (lw != null) {
      l = lw.getLevel();
    }

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.getLevel : category : " + category + " : level : " + l);
    }

    return l;
  }

  /**
   * PUBLIC:  Set the log level to a logger with name space extracted from the given category.
   *
   * @param level value according to the java.util.logging.Levels
   * @param category category
   */
  @Override
  public final void setLevel(final int level, final String category) {
    AccessController.doPrivileged(
      new PrivilegedAction<Object>() {
          public Object run() {
            if (FORCE_INTERNAL_DEBUG) {
              System.out.println(
                "CommonsLoggingSessionLog.setLevel : IN : category : " + category + " to level : " + level);
            }

            final LogWrapper lw = getLogWrapper(category);

            if (lw == null) {
              System.err.println("CommonsLoggingSessionLog.setLevel : category not found : " + category);
            } else {
              lw.setLevel(level);

              Logger l = getLog4JLogger(lw.getLog());

              if (l == null) {
                System.err.println(
                  "CommonsLoggingSessionLog.setLevel : Logger not found : " + category + " : " + lw.getLog());
              } else {
                switch (level) {
                  case SEVERE:
                    l.setLevel(org.apache.log4j.Level.ERROR);

                    break;

                  case WARNING:
                    l.setLevel(org.apache.log4j.Level.WARN);

                    break;

                  case INFO:
                  case CONFIG:
                    l.setLevel(org.apache.log4j.Level.INFO);

                    break;

                  case FINE:
                  case FINER:
                  case FINEST:
                    l.setLevel(org.apache.log4j.Level.DEBUG);

                    break;

                  default:
                    l.setLevel(org.apache.log4j.Level.OFF);
                    System.err.println(
                      "CommonsLoggingSessionLog.setLevel : unknown level : " + level + " : level set to OFF");

                    break;
                }

                if (l.isEnabledFor(org.apache.log4j.Level.WARN)) {
                  l.warn("logger Level set to : " + l.getLevel());
                }

                if (FORCE_INTERNAL_DEBUG) {
                  System.out.println(
                    "CommonsLoggingSessionLog.setLevel : OUT : category : " + category + " to level : " + l.getLevel());
                }
              }
            }

            // nothing to return :
            return null;
          }
        });
  }

  /**
   * PUBLIC:  Set the output stream that will receive the formatted log entries. DO nothing as Log4J manages the
   * appenders (console or files) via Log4J configuration
   *
   * @param fileOutputStream the file output stream will receive the formatted log entries.
   */
  @Override
  public final void setWriter(final OutputStream fileOutputStream) {
    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.setWriter : stream : " + fileOutputStream);
    }

    // do nothing
  }

  /**
   * PUBLIC: Set the session and session namespace.
   *
   * @param pSession an eclipselink Session
   */
  @Override
  public final void setSession(final Session pSession) {
    super.setSession(pSession);

    if (pSession != null) {
      final String sessionName = pSession.getName();

      if ((sessionName != null) && (sessionName.length() != 0)) {
        this.sessionNameSpace = SESSION_ECLIPSELINK_NAMESPACE + "." + sessionName;
      } else {
        this.sessionNameSpace = DEFAULT_ECLIPSELINK_NAMESPACE;
      }

      if (FORCE_INTERNAL_DEBUG) {
        System.out.println("CommonsLoggingSessionLog.setSession : sessionNameSpace : " + this.sessionNameSpace);
      }

      //Initialize loggers eagerly
      addLogger(this.sessionNameSpace, this.sessionNameSpace);

      addDefaultLoggers(this.sessionNameSpace);
    }
  }

  /**
   * PUBLIC: Check if a message of the given lev would actually be logged by the logger with name space built
   * from the given session and category. Return the shouldLog for the given category
   *
   * @param level value according to the java.util.logging.Levels
   * @param category category
   *
   * @return true if the given message will be logged
   */
  @Override
  public final boolean shouldLog(final int level, final String category) {
    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.shouldLog : IN : category : " + category + " : " + level);
    }

    boolean res = false;

    if (level == OFF) {
      res = false;
    } else if (level == ALL) {
      res = true;
    } else {
      final LogWrapper lw = getLogWrapper(category);

      if (lw == null) {
        System.err.println("CommonsLoggingSessionLog.shouldLog : category : " + category + " - NO LOGGER FOUND");
        res = false;
      } else {
        res = level >= lw.getLevel();
      }
    }

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.shouldLog : OUT : category : " + category + " : " + res);
    }

    return res;
  }

  /**
   * PUBLIC: Log a SessionLogEntry
   *
   * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
   */
  public final void log(final SessionLogEntry entry) {
    if (shouldLog(entry.getLevel(), entry.getNameSpace())) {
      if (FORCE_INTERNAL_DEBUG) {
        System.out.println("CommonsLoggingSessionLog.log : message : " + entry.getMessage());
      }

      final Log   log       = getLogger(entry.getNameSpace());
      final Level javaLevel = getJavaLevel(entry.getLevel());

      internalLog(entry, javaLevel, log);
    }
  }

  /**
   * PUBLIC: Log a throwable.
   *
   * @param throwable a throwable
   */
  @Override
  public final void throwing(final Throwable throwable) {
    final Log log = getLogger(null);

    if (log != null) {
      log.error(null, throwable);
    }
  }

  /**
   * INTERNAL: Each session owns its own session log because session is stored in the session log
   *
   * @return value TODO : Value Description
   */
  @Override
  public final Object clone() {
    // There is no special treatment required for cloning here
    // The state of this object is described  by member variables sessionLogger and categoryLoggers.
    // This state depends on session.
    // If session for the clone is going to be the same as session for this there is no
    // need to do "deep" cloning.
    // If not, the session being cloned should call setSession() on its JavaLog object to initialize it correctly.
    final CommonsLoggingSessionLog cloneLog = (CommonsLoggingSessionLog) super.clone();

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.clone : cloned instance : " + cloneLog);
      // to inspect the initialization process :
      new Throwable().printStackTrace(System.out);
    }

    return cloneLog;
  }

  /**
   * INTERNAL: Add Logger to the categoryloggers.
   *
   * @param loggerCategory category
   * @param loggerNameSpace name space
   */
  private final void addLogger(final String loggerCategory, final String loggerNameSpace) {
    if (FORCE_INTERNAL_DEBUG) {
      System.out.println(
        "CommonsLoggingSessionLog.addLogger : category : " + loggerCategory + " in name space : " + loggerNameSpace);
    }

    this.CATEGORY_LOGGERS.put(loggerCategory, new LogWrapper(this, loggerCategory, LogFactory.getLog(loggerNameSpace)));
  }

  /**
   * INTERNAL: Return the name space for the given category from the map.
   *
   * @param category category
   *
   * @return name space for the given category
   */
  private final String getNameSpaceString(final String category) {
    if (getSession() == null) {
      return DEFAULT_ECLIPSELINK_NAMESPACE;
    } else if ((category == null) || (category.length() == 0)) {
      return this.sessionNameSpace;
    } else {
      return this.NAMESPACE_MAP.get(category);
    }
  }

  /**
   * INTERNAL: Return the LogWrapper instance for the given category
   *
   * @param category category
   *
   * @return LogWrapper instance or null if not found
   */
  private final LogWrapper getLogWrapper(final String category) {
    LogWrapper lw = null;

    if (getSession() == null) {
      lw = this.CATEGORY_LOGGERS.get(DEFAULT_ECLIPSELINK_NAMESPACE);
    } else if ((category == null) || (category.length() == 0)) {
      lw = this.CATEGORY_LOGGERS.get(this.sessionNameSpace);
    } else {
      lw = this.CATEGORY_LOGGERS.get(category);
    }

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.getLogWrapper : " + category + " = " + lw);
    }

    return lw;
  }

  /**
   * INTERNAL: Return the Logger instance for the given category
   *
   * @param category category
   *
   * @return value Logger instance or null if not found
   */
  private final Log getLogger(final String category) {
    final LogWrapper lw = getLogWrapper(category);

    Log              log = null;

    if (lw != null) {
      log = lw.getLog();
    }

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.getLogger : log : " + log);
    }

    return log;
  }

  /**
   * INTERNAL: Adds default loggers for the given name space
   *
   * @param namespace name space
   */
  private final void addDefaultLoggers(final String namespace) {
    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.addDefaultLoggers : nameSpace : " + namespace);
    }

    String loggerCategory;
    String loggerNameSpace;

    final String[] categories = SessionLog.loggerCatagories;

    final int size = categories.length;

    for (int i = 0; i < size; i++) {
      loggerCategory = categories[i];
      loggerNameSpace = namespace + "." + loggerCategory;

      this.NAMESPACE_MAP.put(loggerCategory, loggerNameSpace);
      addLogger(loggerCategory, loggerNameSpace);
    }
  }

  /**
   * INTERNAL:  Build a LogRecord
   *
   * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
   * @param level according to eclipselink level
   * @param log commons-logging 1.1 wrapper
   */
  private final void internalLog(final SessionLogEntry entry, final Level level, final Log log) {
    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.internalLog : " + computeMessage(entry, level));
    }

    final int lev = entry.getLevel();

    if (lev == ALL) {
      log.trace(computeMessage(entry, level));
    } else {
      switch (lev) {
        case SEVERE:

          if (log.isErrorEnabled()) {
            log.error(computeMessage(entry, level));
          }

          break;

        case WARNING:

          if (log.isWarnEnabled()) {
            log.warn(computeMessage(entry, level));
          }

          break;

        case INFO:
        case CONFIG:

          if (log.isInfoEnabled()) {
            log.info(computeMessage(entry, level));
          }

          break;

        case FINE:
        case FINER:
        case FINEST:

          if (log.isDebugEnabled()) {
            log.debug(computeMessage(entry, level));
          }

          break;

        default:
          System.err.println("CommonsLoggingSessionLog.internalLog : unknown level : " + lev);

          break;
      }
    }
  }

  /**
   * INTERNAL: Computes the log message
   *
   * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
   * @param level according to eclipselink level
   *
   * @return value TODO : Value Description
   */
  private final String computeMessage(final SessionLogEntry entry, final Level level) {
    // Format message so that we do not depend on the bundle
    final EclipseLinkLogRecord lr = new EclipseLinkLogRecord(level, formatMessage(entry));

    lr.setSourceClassName(null);
    lr.setSourceMethodName(null);
    lr.setLoggerName(getNameSpaceString(entry.getNameSpace()));

    if (shouldPrintSession()) {
      lr.setSessionString(getSessionString(entry.getSession()));
    }

    if (shouldPrintConnection()) {
      lr.setConnection(entry.getConnection());
    }

    lr.setThrown(entry.getException());
    lr.setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace());
    lr.setShouldPrintDate(shouldPrintDate());
    lr.setShouldPrintThread(shouldPrintThread());

    return this.LOG_FORMATTER.format(lr);
  }

  /**
   * INTERNAL: Return the corresponding java.util.logging.Level for a given eclipselink level.
   *
   * @param level according to eclipselink level
   *
   * @return value according to the java.util.logging.Levels
   */
  private static final Level getJavaLevel(final int level) {
    return JAVA_LEVELS[level];
  }

  /**
   * INTERNAL: Returns Log4JLogger instance
   *
   * @param log commons-logging 1.1 wrapper
   *
   * @return Log4JLogger instance
   */
  private static final Logger getLog4JLogger(final Log log) {
    Logger l = null;

    if (log instanceof Log4JLogger) {
      l = ((Log4JLogger) log).getLogger();
    }

    if (FORCE_INTERNAL_DEBUG) {
      System.out.println("CommonsLoggingSessionLog.getLog4JLogger : " + l);
    }

    return l;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * INTERNAL: LogWrapper class wraps the real apache commons logging Log instance
   */
  private static final class LogWrapper {
    //~ Members --------------------------------------------------------------------------------------------------------

    /** parent CommonsLoggingSessionLog instance */
    private final CommonsLoggingSessionLog sessionLog;
    /** category for debug mode */
    private final String category;
    /** apache commons logging Log instance */
    private final Log log;
    /** parent LogWrapper */
    private final LogWrapper parent;
    /** child LogWrapper instances */
    private final List<LogWrapper> children = new ArrayList<LogWrapper>();
    /** level as defined by java.util.logging.Levels. Can be changed at runtime */
    private int level = UNDEFINED_LEVEL;
    /** cached level as defined by java.util.logging.Levels. Extracted from parent LogWrapper instances */
    private int cachedLevel = UNDEFINED_LEVEL;

    //~ Constructors ---------------------------------------------------------------------------------------------------

/**
         * INTERNAL:
         *
         * Constructor
         * 
         * @param log apache commons logging Log instance
         */
    protected LogWrapper(final CommonsLoggingSessionLog sessionLog, final String category, final Log log) {
      this.sessionLog = sessionLog;
      this.category = category;
      this.log = log;

      final Logger l = CommonsLoggingSessionLog.getLog4JLogger(log);

      String       parentName = null;

      if (l != null) {
        parentName = l.getParent().getName();
      }

      if ((parentName != null) && ! "null".equals(parentName)) {
        this.parent = this.sessionLog.getLogWrapper(parentName);

        if (this.parent != null) {
          this.parent.addChild(this);
        }
      } else {
        this.parent = null;
      }
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * INTERNAL: Returns the apache commons logging Log instance
     *
     * @return apache commons logging Log instance
     */
    protected final Log getLog() {
      return this.log;
    }

    /**
     * INTERNAL: Returns the level according to the java.util.logging.Levels
     *
     * @return level
     */
    protected final int getLevel() {
      int res = this.level;

      // if this level is undefined : compute it from parents :
      if (res == UNDEFINED_LEVEL) {
        if (this.cachedLevel == UNDEFINED_LEVEL) {
          res = computeLevel(this.level);

          if (USE_INTERNAL_CACHE) {
            this.cachedLevel = res;
          }
        } else {
          res = this.cachedLevel;
        }
      }

      return res;
    }

    /**
     * INTERNAL: Defines the level according to the java.util.logging.Levels
     *
     * @param level value according to the java.util.logging.Levels
     */
    protected final void setLevel(final int level) {
      this.level = level;

      if (USE_INTERNAL_CACHE) {
        // reset cachedLevel for all children :
        for (final LogWrapper cw : children) {
          if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.LogWrapper.setLevel : reset cachedLevel for : " + cw.category);
          }

          cw.cachedLevel = UNDEFINED_LEVEL;
        }
      }
    }

    /**
     * INTERNAL: Adds a child LogWrapper
     *
     * @param lw
     */
    protected final void addChild(final LogWrapper lw) {
      this.children.add(lw);

      if (FORCE_INTERNAL_DEBUG) {
        System.out.println(
          "CommonsLoggingSessionLog.LogWrapper.addChild : IN : this : " + this.category + " : child : " + lw.category);
      }
    }

    /**
     * INTERNAL: Computes the cached Level
     *
     * @param localLevel this.level copy
     *
     * @return level
     */
    private final int computeLevel(final int localLevel) {
      LogWrapper pw;
      LogWrapper lw = this;

      if (FORCE_INTERNAL_DEBUG) {
        System.out.println(
          "CommonsLoggingSessionLog.LogWrapper.computeLevel : IN : " + this.category + " : level : " + localLevel);
      }

      while ((lw != null) &&
               (((lw == this) && (localLevel == UNDEFINED_LEVEL)) ||
                 ((lw != this) && (lw.getLevel() == UNDEFINED_LEVEL)))) {
        pw = lw.parent;

        if (pw != lw) {
          lw = pw;
        } else {
          // exit from loop :
          lw = null;
        }
      }

      int l = OFF;

      if (lw != null) {
        l = lw.getLevel();
      }

      if (FORCE_INTERNAL_DEBUG) {
        System.out.println(
          "CommonsLoggingSessionLog.LogWrapper.computeLevel : OUT : " + this.category + " : level : " + l);
      }

      return l;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
