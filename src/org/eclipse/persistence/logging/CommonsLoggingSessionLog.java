package org.eclipse.persistence.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.impl.Log4JLogger;

import org.apache.log4j.Logger;

import org.eclipse.persistence.sessions.Session;

import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


/**
 * PUBLIC:<p>This is a wrapper class for org.apache.commons.logging.Log.  It is used when messages need to be
 * logged through apache commons logging 1.1.</p>
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

  /** Stores the default session name in case there is the session name is missing. */
  public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
  /** TODO : Field Description */
  protected static final String LOGGING_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource";
  /** TODO : Field Description */
  protected static final String TRACE_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource";
  /** TODO : Field Description */
  public static final String DEFAULT_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".default";
  /** TODO : Field Description */
  public static final String SESSION_TOPLINK_NAMESPACE = TOPLINK_NAMESPACE + ".session";
  /** Stores all the java.util.logging.Levels.  The indexes are TopLink logging levels. */
  private static final Level[] levels = new Level[] {
                                          Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO,
                                          Level.WARNING, Level.SEVERE, Level.OFF
                                        };
  /** TODO : Field Description */
  public static final int UNDEFINED_LEVEL = -1;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * Represents the HashMap that stores all the name space strings. The keys are category names.  The values are
   * namespace strings.
   */
  private final Map<String, String> nameSpaceMap = new HashMap<String, String>();
  /** Stores the namespace for session, i.e."org.eclipse.persistence.session.$sessionname$". */
  private String sessionNameSpace;
  /** Log instances */
  private final Map<String, LogWrapper> categoryloggers = new ConcurrentHashMap<String, LogWrapper>();
  /** formats the EclipseLinkLogRecords */
  private final LogFormatter formatter = new LogFormatter();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * INTERNAL:
   */
  public CommonsLoggingSessionLog() {
    super();
    addLogger(DEFAULT_TOPLINK_NAMESPACE, DEFAULT_TOPLINK_NAMESPACE);

    setShouldPrintSession(false);
    setShouldPrintDate(false);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * INTERNAL: Add Logger to the catagoryloggers.
   *
   * @param loggerCategory
   * @param loggerNameSpace
   */
  protected void addLogger(final String loggerCategory, final String loggerNameSpace) {
    //System.err.println("addLogger : " + loggerCategory + " in " + loggerNameSpace);
    categoryloggers.put(loggerCategory, new LogWrapper(LogFactory.getLog(loggerNameSpace)));
  }

  /**
   * PUBLIC:<p>Return the effective lw lev for the name space extracted from session and category. If a Logger's
   * lev is set to be null then the Logger will use an effective Level that will be obtained by walking up the parent
   * tree and using the first non-null Level.</p>
   *
   * @param category
   *
   * @return the effective lw lev.
   */
  @Override
  public int getLevel(final String category) {
    LogWrapper lw = getLogWrapper(category);

    //    System.err.println("getLevel : " + category + " : " + lw.getLevel());
    LogWrapper pw;

    while ((lw != null) && (lw.getLevel() == UNDEFINED_LEVEL)) {
      pw = getLogWrapper(lw.getParentName());
      lw = (pw != lw) ? pw : null;
    }

    if (lw == null) {
      return OFF;
    }

    //System.err.println("getLevel : " + category + " : " + lw.getLevel());
    return lw.getLevel();
  }

  /**
   * PUBLIC:<p>Set the lw lev to a logger with name space extracted from the given category.</p>
   *
   * @param level
   * @param category
   */
  @Override
  public void setLevel(final int level, final String category) {
    //System.err.println("setLevel : " + category + " to " + level);
    final LogWrapper lw = getLogWrapper(category);

    //System.err.println("setLevel : " + lw.getLevel());
    lw.setLevel(level);

    Logger l = getLog4JLogger(lw.getLog());

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
        System.err.println("CommonsLoggingSessionLog.setLevel() : unknown level : " + level);
    }

    l.error("log.level changed to : " + l.getLevel());

    //    System.err.println("setLevel : " + l.getLevel());
  }

  /**
   * PUBLIC:<p>Set the output stream  that will receive the formatted lw entries.</p>
   *
   * @param fileOutputStream the file output stream will receive the formatted lw entries.
   */
  @Override
  public void setWriter(final OutputStream fileOutputStream) {
    // do nothing
  }

  /**
   * INTERNAL: Return the name space for the given category from the map.
   *
   * @param category
   *
   * @return value TODO : Value Description
   */
  protected String getNameSpaceString(final String category) {
    if (session == null) {
      return DEFAULT_TOPLINK_NAMESPACE;
    } else if ((category == null) || (category.length() == 0)) {
      return sessionNameSpace;
    } else {
      return nameSpaceMap.get(category);
    }
  }

  /**
   * INTERNAL: Return the Logger for the given category
   *
   * @param category
   *
   * @return value TODO : Value Description
   */
  public LogWrapper getLogWrapper(final String category) {
    LogWrapper lw = null;

    if (session == null) {
      lw = categoryloggers.get(DEFAULT_TOPLINK_NAMESPACE);
    } else if ((category == null) || (category.length() == 0)) {
      lw = categoryloggers.get(sessionNameSpace);
    } else {
      lw = categoryloggers.get(category);

      // If session != null, categoryloggers should have an entry for this category
      assert lw != null;
    }

    return lw;
  }

  /**
   * INTERNAL: Return the Logger for the given category
   *
   * @param category
   *
   * @return value TODO : Value Description
   */
  protected Log getLogger(final String category) {
    final LogWrapper lw = getLogWrapper(category);

    return (lw != null) ? lw.getLog() : null;
  }

  /**
   * PUBLIC:<p>Set the session and session namespace.</p>
   *
   * @param session a Session
   */
  @Override
  public void setSession(final Session session) {
    super.setSession(session);

    if (session != null) {
      final String sessionName = session.getName();

      if ((sessionName != null) && (sessionName.length() != 0)) {
        sessionNameSpace = SESSION_TOPLINK_NAMESPACE + "." + sessionName;
      } else {
        sessionNameSpace = DEFAULT_TOPLINK_NAMESPACE;
      }

      //Initialize loggers eagerly
      addLogger(sessionNameSpace, sessionNameSpace);

      String loggerCategory;
      String loggerNameSpace;

      for (int i = 0, size = loggerCatagories.length; i < size; i++) {
        loggerCategory = loggerCatagories[i];
        loggerNameSpace = sessionNameSpace + "." + loggerCategory;

        nameSpaceMap.put(loggerCategory, loggerNameSpace);
        addLogger(loggerCategory, loggerNameSpace);
      }
    }
  }

  /**
   * INTERNAL: Return the corresponding org.apache.commons.logging.Level for a given TopLink lev.
   *
   * @param level
   *
   * @return value TODO : Value Description
   */
  protected final Level getJavaLevel(final int level) {
    return levels[level];
  }

  /**
   * PUBLIC:<p>Check if a message of the given lev would actually be logged by the logger with name space built
   * from the given session and category. Return the shouldLog for the given category from</p>
   *
   * @param level
   * @param category
   *
   * @return true if the given message lev will be logged
   */
  @Override
  public boolean shouldLog(final int level, final String category) {
    //System.err.println("shouldLog : " + category + " to " + level);
    if (level == OFF) {
      return false;
    }

    if (level == ALL) {
      // Level.ALL
      return true;
    }

    LogWrapper lw = getLogWrapper(category);

    //System.err.println("shouldLog : " + lw.getLevel());
    LogWrapper pw;

    while ((lw != null) && (lw.getLevel() == UNDEFINED_LEVEL)) {
      pw = getLogWrapper(lw.getParentName());
      lw = (pw != lw) ? pw : null;
    }

    if (lw == null) {
      return false;
    }

    //System.err.println("shouldLog : " + category + " : " + lw.getLevel());
    return level >= lw.getLevel();
  }

  /**
   * PUBLIC:<p>Log a SessionLogEntry</p>
   *
   * @param entry SessionLogEntry that holds all the information for a TopLink logging event
   */
  public void log(final SessionLogEntry entry) {
    //    System.err.println("lw : "+entry.getMessage());
    if (! shouldLog(entry.getLevel(), entry.getNameSpace())) {
      return;
    }

    final Log log = getLogger(entry.getNameSpace());

    internalLog(entry, log);
  }

  /**
   * INTERNAL:<p>Build a LogRecord</p>
   *
   * @param entry SessionLogEntry that holds all the information for a TopLink logging event
   * @param log commons-logging 1.1 wrapper
   */
  protected void internalLog(final SessionLogEntry entry, final Log log) {
    // Format message so that we do not depend on the bundle
    final EclipseLinkLogRecord lr = new EclipseLinkLogRecord(getJavaLevel(entry.getLevel()), formatMessage(entry));

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

    // date is already present in log4J pattern :
    //    lr.setShouldPrintDate(false);
    final String msg = formatter.format(lr);

    //    System.err.println("internalLog : "+msg);
    final int lev = entry.getLevel();

    if (lev == ALL) {
      log.trace(msg);

      return;
    }

    switch (lev) {
      case SEVERE:
        log.error(msg);

        return;

      case WARNING:
        log.warn(msg);

        return;

      case INFO:
      case CONFIG:
        log.info(msg);

        return;

      case FINE:
      case FINER:
      case FINEST:

        if (log.isDebugEnabled()) {
          log.debug(msg);
        }

        return;

      default:
        System.err.println("CommonsLoggingSessionLog.internalLog() : unknown level : " + lev);
    }
  }

  /**
   * PUBLIC:<p>Log a throwable.</p>
   *
   * @param throwable a throwable
   */
  @Override
  public void throwing(final Throwable throwable) {
    getLogger(null).error(null, throwable);
  }

  /**
   * INTERNAL: Each session owns its own session lw because session is stored in the session lw
   *
   * @return value TODO : Value Description
   */
  @Override
  public Object clone() {
    // There is no special treatment required for cloning here
    // The state of this object is described  by member variables sessionLogger and categoryLoggers.
    // This state depends on session.
    // If session for the clone is going to be the same as session for this there is no
    // need to do "deep" cloning.
    // If not, the session being cloned should call setSession() on its CommonsLoggingSessionLog object to initialize it correctly.
    return super.clone();
  }

  /**
   * Returns Log4JLogger
   *
   * @param l Log
   *
   * @return Log4JLogger
   */
  public static Logger getLog4JLogger(final Log l) {
    if (l instanceof Log4JLogger) {
      return ((Log4JLogger) l).getLogger();
    }

    return null;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  private static final class LogWrapper {
    //~ Members --------------------------------------------------------------------------------------------------------

    private final Log    log;
    private final String parentName;

    // level can be changed :
    private int level = UNDEFINED_LEVEL;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public LogWrapper(final Log log) {
      this.log = log;

      final Logger c    = CommonsLoggingSessionLog.getLog4JLogger(log);
      final String name = (c != null) ? c.getParent().getName() : null;

      this.parentName = ((name != null) && ! "null".equals(name)) ? name : null;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public Log getLog() {
      return log;
    }

    public int getLevel() {
      return level;
    }

    public void setLevel(final int level) {
      this.level = level;
    }

    public String getParentName() {
      return parentName;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
