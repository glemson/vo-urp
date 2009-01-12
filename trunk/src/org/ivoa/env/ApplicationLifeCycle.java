package org.ivoa.env;

import org.ivoa.conf.Configuration;


/**
 * Simple class to manage internal start / stop / terminate events
 *
 * @author laurent bourges (voparis)
 */
public final class ApplicationLifeCycle {

  /**
   * Private Constructor
   */
  private ApplicationLifeCycle() {
  }

  /**
   * Called when application stops
   */
  protected static void doStart() {
    final String title = Configuration.getInstance().getTitle();
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
    final String title = Configuration.getInstance().getTitle();
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println(" " + title + " : stop ...");
    System.out.println("-------------------------------------------------------------------------------");
    
    ClassLoaderCleaner.clean();
  }

  /**
   * Called when JVM ends
   */
  protected static void onEnd() {
    final String title = Configuration.getInstance().getTitle();
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println(" " + title + " : terminated.");
    System.out.println("-------------------------------------------------------------------------------");
  }
}
