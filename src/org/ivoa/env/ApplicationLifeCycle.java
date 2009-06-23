package org.ivoa.env;

import org.ivoa.conf.Configuration;


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

    System.out.println("-------------------------------------------------------------------------------");
    System.out.println(" " + title + " : start ...");
    System.out.println("-------------------------------------------------------------------------------");
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
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println(" " + APPLICATION_TITLE + " : stop ...");
    System.out.println("-------------------------------------------------------------------------------");
  }

  /**
   * Called when JVM ends
   */
  protected static void onEnd() {
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println(" " + APPLICATION_TITLE + " : terminated.");
    System.out.println("-------------------------------------------------------------------------------");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
