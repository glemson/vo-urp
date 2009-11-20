package org.apache.log4j;

import java.util.Enumeration;
import org.apache.log4j.spi.LoggerRepository;


/**
 * This class manages the shutdown of the Log4J library to close all appenders on shutdown.<br/>
 * Closing Appenders avoids exceptions when appenders are finalized.<br/>
 *
 * This code is derived from the org.apache.log4j.LogManager.shutdown()
 * 
 * @author laurent bourges (voparis) : bourges.laurent@gmail.com
 */
public final class Log4JCleaner {

  /**
   * Forbidden constructor
   */
  private Log4JCleaner() {
    /* no-op */
  }

  /**
   * Proper Log4J shutdown :<br/>
   * Close all appenders at shutdown
   */
  public final static void shutdown() {
    final LoggerRepository lr = LogManager.getLoggerRepository();
    if (lr != null) {
      Logger c;
      final Logger root = lr.getRootLogger();

      if (root != null) {
        // begin by closing nested appenders :
        //    root.closeNestedAppenders();
        closeNestedAppenders(root);

        Enumeration cats = lr.getCurrentLoggers();
        while (cats.hasMoreElements()) {
          c = (Logger) cats.nextElement();
          closeNestedAppenders(c);
        }

        // then, remove all appenders
        root.removeAllAppenders();

        cats = lr.getCurrentLoggers();
        while (cats.hasMoreElements()) {
          c = (Logger) cats.nextElement();
          c.removeAllAppenders();
        }
      }

      // force GC :
      if (lr instanceof Hierarchy) {
        ((Hierarchy) lr).clear();
      }
    }
  }

  /**
   * Close all attached appenders implementing the AppenderAttachable interface.
   * @param logger logger to clean up
   */
  private final static void closeNestedAppenders(final Logger logger) {
    final Enumeration enumeration = logger.getAllAppenders();
    if (enumeration != null) {
      Appender a;
      while (enumeration.hasMoreElements()) {
        a = (Appender) enumeration.nextElement();

// bug here :
//      if (a instanceof AppenderAttachable) {
        a.close();
//      }
      }
    }
  }
}
