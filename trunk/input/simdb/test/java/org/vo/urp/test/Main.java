package org.vo.urp.test;

import org.apache.commons.logging.Log;

import org.ivoa.env.ApplicationAdapter;

import org.ivoa.util.timer.TimerFactory;
import org.vo.urp.test.jaxb.XMLTests;

/**
 * Unit Tests : JPA
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class Main {
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new instance of Main
   */
  private Main() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Main Entry point for test classes
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    // Initializes logging :
    final Log log = initLogs();

    if (log != null) {
      ApplicationAdapter.start();

      final long start = System.nanoTime();

      try {
        new XMLTests().run(args);

        new DBTests().run(args);

      } finally {
        final long stop = System.nanoTime();
        TimerFactory.getTimer("Test.main").addMilliSeconds(start, stop);

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
}
//~ End of file --------------------------------------------------------------------------------------------------------
