package org.ivoa.env;

import org.ivoa.conf.Configuration;
import org.ivoa.util.SystemLogUtil;


/**
 * Simple class to manage internal start / stop / terminate events
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ApplicationLifeCycle {
    
    /** application title copied in doStart() method */
    private static String APPLICATION_TITLE = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Private Constructor
   */
  private ApplicationLifeCycle() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Called when application stops
   */
  protected static void doStart() {
    final String title = Configuration.getInstance().getTitle();

    // memorize the application title to avoid using Configuration.getInstance() :
    APPLICATION_TITLE = title;

    if (SystemLogUtil.isInfoEnabled()) {
      SystemLogUtil.info(" " + title + " : start ...");
    }
  }

  /**
   * Called when application stops
   */
  public static void doExit() {
    System.exit(0);
  }

  /**
   * Called when JVM exit
   */
  public static void onExit() {
    if (SystemLogUtil.isInfoEnabled()) {
      SystemLogUtil.info(" " + APPLICATION_TITLE + " : stop ...");
    }
  }

  /**
   * Called when JVM ends
   */
  protected static void onEnd() {
    if (SystemLogUtil.isInfoEnabled()) {
      SystemLogUtil.info(" " + APPLICATION_TITLE + " : terminated.");
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
