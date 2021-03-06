package org.ivoa.dm;

import org.apache.commons.logging.Log;
import org.ivoa.env.ApplicationAdapter;
import org.ivoa.util.SystemLogUtil;

/**
 * DataModelManager Command Line Tool
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ManagerMain {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** TODO : Field Description */
    public static final String COMMAND_VALIDATE = "validate";
    /** TODO : Field Description */
    public static final String COMMAND_LOAD = "load";
    /** TODO : Field Description */
    public static final String COMMAND_DELETE = "delete";
    /** TODO : Field Description */
    public static final String COMMAND_GET = "get";
    /** TODO : Field Description */
    private static Log log = null;

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Forbidden Constructor
     */
    private ManagerMain() {
        /* no-op */
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Main Entry point for test classes
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        // Initializes logging :
        log = initLogs();

        if (log != null) {
            ApplicationAdapter.start();

            try {
                process(args);
            } finally {
                ApplicationAdapter.stop();
            }
        }
    }

    /**
     * Initializes Log4J
     *
     * @return Log instance
     */
    private static Log initLogs() {
        Log l = null;

        try {
            l = org.ivoa.util.LogUtil.getLogger();
        } catch (final Throwable th) {
          if (SystemLogUtil.isErrorEnabled()) {
            SystemLogUtil.error("Unable to initialize logging system :", th);
          }
        }

        return l;
    }

    /**
     * TODO : Method Description
     *
     * @param args
     */
    private static void process(final String[] args) {
        if ((args == null) || (args.length <= 1)) {
            log.error("Manager : missing arguments !");
            showHelp();

            return;
        }

        final String cmd = args[0];

        if (log.isWarnEnabled()) {
            log.warn("Manager : command : " + cmd);
        }

        final String jpa_pu = args[1];

        if (log.isWarnEnabled()) {
            log.warn("JPA PU : " + jpa_pu);
        }

        if (COMMAND_VALIDATE.equals(cmd)) {
            if (args.length < 3) {
                log.error("Manager : missing file argument !");
                showHelp();

                return;
            }

            final String fileName = args[2];

            DataModelManager dmm = new DataModelManager(jpa_pu);

            dmm.validate(fileName);
        } else if (COMMAND_LOAD.equals(cmd)) {
            if (args.length < 3) {
                log.error("Manager : missing file argument !");
                showHelp();

                return;
            }

            final String fileName = args[2];

            DataModelManager dmm = new DataModelManager(jpa_pu);

            dmm.load(fileName, "");
        } else {
            log.error("Manager : unsupported command : " + cmd);
        }
    }

    /**
     * TODO : Method Description
     */
    private static void showHelp() {
        if (log.isWarnEnabled()) {
            log.warn("Supported arguments are : [command] [options]");
            log.warn("Valid commands & options : ");
            log.warn("- [load] [jpa_pu] [file]     : loads the xml file and loads the object to the database for the specified JPA persistence unit");
            log.warn("- [validate] [jpa_pu] [file] : validates the xml file against the DM Schema. Includes [TBD does it?] validation against the existing database for the specified JPA persistence unit");
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
