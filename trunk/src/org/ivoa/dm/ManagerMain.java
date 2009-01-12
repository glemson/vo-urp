package org.ivoa.dm;

import org.apache.commons.logging.Log;
import org.ivoa.env.ApplicationAdapter;

/**
 * DataModelManager Command Line Tool
 *
 * @author laurent bourges (voparis)
 */
public final class ManagerMain {
  
  public final static String COMMAND_VALIDATE = "validate";
  public final static String COMMAND_LOAD = "load";
  public final static String COMMAND_DELETE = "delete";
  public final static String COMMAND_GET = "get";
  
  private static Log log = null;
  
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Forbidden Constructor
   */
  private ManagerMain() {
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
      ApplicationAdapter.init();
      
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
      System.err.println("Unable to initialize logging system :");
      th.printStackTrace(System.err);
    }

    return l;
  }
  
  private static void process(final String[] args) {
    if (args == null || args.length <= 1) {
      log.error("Manager : missing arguments !");
      showHelp();
      return;
    }
    final String cmd = args[0];
    log.warn("Manager : command : "+cmd);
    final String jpa_pu = args[1];
    log.warn("JPA PU : "+jpa_pu);
    
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
      dmm.load(fileName);
      
    } else {
      log.error("Manager : unsupported command : "+cmd);
    }
  }
  
  private static void showHelp() {
    log.warn("Supported arguments are : [command] [options]");
    log.warn("Valid commands & options : ");
    log.warn("- [load] [jpa_pu] [file]     : loads the xml file and loads the object to the database for the specified JPA persistence unit");
    log.warn("- [validate] [jpa_pu] [file] : validates the xml file against the DM Schema. Includes [TBD does it?] validation against the existing database for the specified JPA persistence unit");
  }
}
