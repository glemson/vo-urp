package org.eclipse.persistence.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.impl.Log4JLogger;

import org.apache.log4j.Logger;

import org.eclipse.persistence.sessions.Session;

import java.io.OutputStream;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * PUBLIC:
 * This is a wrapper class for org.apache.commons.logging.Log.  It is used when messages need to be
 * logged through apache commons logging 1.1.
 * 
 * 
 * History :
 * 05/05/2009 : Updated API to EclipseLink 1.1 version + Javadoc fixes
 * 
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
    /** internal debugger FLAG */
    private static final boolean FORCE_INTERNAL_DEBUG = false;
    /** value corresponding to an undefined Level */
    public static final int UNDEFINED_LEVEL = -1;
    /**
     * Stores the default session name in case there is the session name is missing.
     * org.eclipse.persistence package used by Log4J configuration
     */
    public static final String ECLIPSELINK_NAMESPACE = "org.eclipse.persistence";
    /** org.eclipse.persistence.default used by Log4J configuration */
    public static final String DEFAULT_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + ".default";
    /** org.eclipse.persistence.session used by Log4J configuration */
    public static final String SESSION_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + ".session";
    /**
     * Copied from JavaLog for compatibility issues
     */
    public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
    /**
     * Copied from JavaLog for compatibility issues
     */
    protected static final String LOGGING_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.LoggingLocalizationResource";
    /**
     * Copied from JavaLog for compatibility issues
     */
    protected static final String TRACE_LOCALIZATION_STRING = "org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource";
    /**
     * Stores all the java.util.logging.Levels.  The indexes are TopLink logging levels.
     */
    private static final Level[] levels = new Level[]{Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF};

    //~ Members ----------------------------------------------------------------------------------------------------------
    /**
     * Represents the HashMap that stores all the name space strings.
     * The keys are category names.  The values are namespace strings.
     */
    private final Map<String, String> nameSpaceMap = new ConcurrentHashMap<String, String>();
    /**
     * Stores the namespace for session, i.e."org.eclipse.persistence.session.<sessionname>".
     */
    private String sessionNameSpace;
    /** Log instances */
    private final Map<String, LogWrapper> categoryloggers = new ConcurrentHashMap<String, LogWrapper>();
    /** formats the EclipseLinkLogRecords */
    private final LogFormatter formatter = new LogFormatter();

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * INTERNAL:
     * CommonsLoggingSessionLog Constructor. This adds a root logger for DEFAULT_ECLIPSELINK_NAMESPACE.
     */
    public CommonsLoggingSessionLog() {
        super();
        addLogger(DEFAULT_ECLIPSELINK_NAMESPACE, DEFAULT_ECLIPSELINK_NAMESPACE);

        setShouldPrintSession(false);
        setShouldPrintDate(false);
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * INTERNAL:
     * Add Logger to the catagoryloggers.
     *
     * @param loggerCategory category
     * @param loggerNameSpace name space
     */
    protected void addLogger(final String loggerCategory, final String loggerNameSpace) {
        if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.addLogger : category : " + loggerCategory + " in name space : " + loggerNameSpace);
        }
        categoryloggers.put(loggerCategory, new LogWrapper(LogFactory.getLog(loggerNameSpace)));
    }

    /**
     * INTERNAL:
     * Return catagoryloggers
     */
    public Map getCategoryLoggers() {
        return categoryloggers;
    }

    /**
     * PUBLIC:
     * 
     * Return the effective log level for the name space extracted from session and category.
     * If a Logger's level is set to be null then the Logger will use an effective Level that will
     * be obtained by walking up the parent tree and using the first non-null Level.
     * 
     *
     * @param category category
     *
     * @return the effective level according to the java.util.logging.Levels
     * 
     */
    @Override
    public int getLevel(final String category) {
        LogWrapper lw = getLogWrapper(category);

        LogWrapper pw;

        while (lw != null && lw.getLevel() == UNDEFINED_LEVEL) {
            pw = getLogWrapper(lw.getParentName());
            lw = (pw != lw) ? pw : null;
        }

        if (lw == null) {
            if (FORCE_INTERNAL_DEBUG) {
                System.out.println("CommonsLoggingSessionLog.getLevel : category : " + category + " : OFF");
            }
            return OFF;
        }

        if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.getLevel : category : " + category + " : level : " + lw.getLevel());
        }
        return lw.getLevel();
    }

    /**
     * PUBLIC:
     * 
     * Set the log level to a logger with name space extracted from the given category.
     * 
     *
     * @param level value according to the java.util.logging.Levels
     * @param category category
     */
    @Override
    public void setLevel(final int level, final String category) {
        if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.setLevel : category : " + category + " to level : " + level);
        }
        final LogWrapper lw = getLogWrapper(category);

        AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
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
                        System.err.println("CommonsLoggingSessionLog.setLevel : unknown level : " + level);
                }

                l.error("logger Level set to : " + l.getLevel());

                if (FORCE_INTERNAL_DEBUG) {
                    System.out.println("CommonsLoggingSessionLog.setLevel : category : " + category + " to level : " + l.getLevel());
                }
                return null; // nothing to return
            }
        });

    }

    /**
     * PUBLIC:
     * 
     * Set the output stream  that will receive the formatted log entries.
     * 
     *
     * DO nothing as Log4J manages the appenders (console or files) via Log4J configuration
     *
     * @param fileOutputStream the file output stream will receive the formatted log entries.
     * 
     */
    @Override
    public void setWriter(final OutputStream fileOutputStream) {
        // do nothing
    }

    /**
     * INTERNAL: Return the name space for the given category from the map.
     *
     * @param category category
     *
     * @return name space for the given category
     */
    protected String getNameSpaceString(final String category) {
        if (session == null) {
            return DEFAULT_ECLIPSELINK_NAMESPACE;
        } else if (category == null || category.length() == 0) {
            return sessionNameSpace;
        } else {
            return nameSpaceMap.get(category);
        }
    }

    /**
     * INTERNAL: Return the LogWrapper instance for the given category
     *
     * @param category category
     *
     * @return LogWrapper instance or null if not found
     */
    private LogWrapper getLogWrapper(final String category) {
        LogWrapper lw = null;

        if (session == null) {
            lw = categoryloggers.get(DEFAULT_ECLIPSELINK_NAMESPACE);
        } else if (category == null || category.length() == 0) {
            lw = categoryloggers.get(sessionNameSpace);
        } else {
            lw = categoryloggers.get(category);

            // If session != null, categoryloggers should have an entry for this category
            assert lw != null;
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
    protected Log getLogger(final String category) {
        final LogWrapper lw = getLogWrapper(category);

        return (lw != null) ? lw.getLog() : null;
    }

    /**
     * PUBLIC:Set the session and session namespace.
     *
     * @param session an eclipselink Session
     */
    @Override
    public void setSession(final Session session) {
        super.setSession(session);

        if (session != null) {
            final String sessionName = session.getName();

            if (sessionName != null && sessionName.length() != 0) {
                sessionNameSpace = SESSION_ECLIPSELINK_NAMESPACE + "." + sessionName;
            } else {
                sessionNameSpace = DEFAULT_ECLIPSELINK_NAMESPACE;
            }

            if (FORCE_INTERNAL_DEBUG) {
                System.out.println("CommonsLoggingSessionLog.setSession : sessionNameSpace : " + sessionNameSpace);
            }
            //Initialize loggers eagerly
            addLogger(sessionNameSpace, sessionNameSpace);

            addDefaultLoggers(sessionNameSpace);
        }
    }

    /**
     * Adds default loggers for the given name space
     * @param namespace name space
     */
    private void addDefaultLoggers(final String namespace) {
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

            nameSpaceMap.put(loggerCategory, loggerNameSpace);
            addLogger(loggerCategory, loggerNameSpace);
        }

    }

    /**
     * INTERNAL: Return the corresponding org.apache.commons.logging.Level for a given eclipselink level.
     *
     * @param level according to eclipselink level
     *
     * @return value according to the java.util.logging.Levels
     */
    protected final static Level getJavaLevel(final int level) {
        return levels[level];
    }

    /**
     * PUBLIC:Check if a message of the given lev would actually be logged by the logger with name space built
     * from the given session and category. Return the shouldLog for the given category from</p>
     *
     * @param level value according to the java.util.logging.Levels
     * @param category category
     *
     * @return true if the given message will be logged
     */
    @Override
    public boolean shouldLog(final int level, final String category) {
        if (level == OFF) {
            return false;
        }

        if (level == ALL) {
            // Level.ALL
            return true;
        }

        LogWrapper lw = getLogWrapper(category);

        LogWrapper pw;

        while (lw != null && lw.getLevel() == UNDEFINED_LEVEL) {
            pw = getLogWrapper(lw.getParentName());
            lw = (pw != lw) ? pw : null;
        }

        if (lw == null) {
            if (FORCE_INTERNAL_DEBUG) {
                System.out.println("CommonsLoggingSessionLog.shouldLog : category : " + category + " - NO LOGGER FOUND");
            }
            return false;
        }

        final boolean res = level >= lw.getLevel();
        if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.shouldLog : category : " + category + " : " + res);
        }
        return res;
    }

    /**
     * PUBLIC:Log a SessionLogEntry</p>
     *
     * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
     */
    public void log(final SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel(), entry.getNameSpace())) {
            return;
        }
        if (FORCE_INTERNAL_DEBUG) {
            System.out.println("CommonsLoggingSessionLog.log : message : " + entry.getMessage());
        }

        final Log log = getLogger(entry.getNameSpace());
        final Level javaLevel = getJavaLevel(entry.getLevel());

        internalLog(entry, javaLevel, log);
    }

    /**
     * INTERNAL:
     * 
     * Build a LogRecord
     * 
     * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
     * @param level according to eclipselink level
     * @param log commons-logging 1.1 wrapper
     * 
     */
    protected void internalLog(final SessionLogEntry entry, final Level level, final Log log) {
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

                    return;

                case WARNING:
                    if (log.isWarnEnabled()) {
                        log.warn(computeMessage(entry, level));
                    }

                    return;

                case INFO:
                case CONFIG:
                    if (log.isInfoEnabled()) {
                        log.info(computeMessage(entry, level));
                    }

                    return;

                case FINE:
                case FINER:
                case FINEST:

                    if (log.isDebugEnabled()) {
                        log.debug(computeMessage(entry, level));
                    }

                    return;

                default:
                    System.err.println("CommonsLoggingSessionLog.internalLog : unknown level : " + lev);
            }
        }
    }

    /**
     * INTERNAL:Computes the log message</p>
     *
     * @param entry SessionLogEntry that holds all the information for a EclipseLink logging event
     * @param level according to eclipselink level
     */
    protected final String computeMessage(final SessionLogEntry entry, final Level level) {
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

        return formatter.format(lr);
    }

    /**
     * PUBLIC:Log a throwable.
     *
     * @param throwable a throwable
     */
    @Override
    public void throwing(final Throwable throwable) {
        getLogger(null).error(null, throwable);
    }

    /**
     * INTERNAL:
     * Each session owns its own session log because session is stored in the session log
     */
    @Override
    public Object clone() {
        // There is no special treatment required for cloning here
        // The state of this object is described  by member variables sessionLogger and categoryLoggers.
        // This state depends on session.
        // If session for the clone is going to be the same as session for this there is no
        // need to do "deep" cloning.
        // If not, the session being cloned should call setSession() on its JavaLog object to initialize it correctly.
        CommonsLoggingSessionLog cloneLog = (CommonsLoggingSessionLog) super.clone();
        return cloneLog;
    }

    /**
     * Returns Log4JLogger instance
     *
     * @param log commons-logging 1.1 wrapper
     *
     * @return Log4JLogger instance
     */
    private static Logger getLog4JLogger(final Log log) {
        if (log instanceof Log4JLogger) {
            return ((Log4JLogger) log).getLogger();
        }

        return null;
    }

    //~ Inner Classes ----------------------------------------------------------------------------------------------------
    /**
     * LogWrapper class wraps the real apache commons logging Log instance
     */
    private static final class LogWrapper {
        //~ Members --------------------------------------------------------------------------------------------------------

        /** apache commons logging Log instance */
        private final Log log;
        /** parent name */
        private final String parentName;
        /** level as defined by java.util.logging.Levels. Can be changed at runtime */
        private int level = UNDEFINED_LEVEL;

        //~ Constructors ---------------------------------------------------------------------------------------------------
        /**
         * Constructor
         * @param log apache commons logging Log instance
         */
        protected LogWrapper(final Log log) {
            this.log = log;

            final Logger c = CommonsLoggingSessionLog.getLog4JLogger(log);
            final String name = (c != null) ? c.getParent().getName() : null;

            this.parentName = (name != null && !"null".equals(name)) ? name : null;
        }

        //~ Methods --------------------------------------------------------------------------------------------------------
        /**
         * Returns the apache commons logging Log instance
         * @return apache commons logging Log instance
         */
        public Log getLog() {
            return log;
        }

        /**
         * Returns the level according to the java.util.logging.Levels
         * @return level
         */
        public int getLevel() {
            return level;
        }

        /**
         * Defines the level according to the java.util.logging.Levels
         * @param level value according to the java.util.logging.Levels
         */
        public void setLevel(final int level) {
            this.level = level;
        }

        /**
         * Returns the parent name
         * @return parent name
         */
        public String getParentName() {
            return parentName;
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
