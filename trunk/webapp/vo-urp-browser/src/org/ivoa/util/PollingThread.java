package org.ivoa.util;

import java.util.concurrent.CountDownLatch;
import org.apache.commons.logging.Log;


/**
 * Simple Looping Thread that calls the handle() method then waits some time and loops till Thread.interrupt() sent by another thread
 *
 * @author laurent
 */
public abstract class PollingThread extends Thread {

  /** DEBUG mode */
  private final static boolean DEBUG = false;
  /** Logger for this class and subclasses */
  protected Log log = LogUtil.getLoggerDev();
  /** wait time in milliseconds. */
  private long wait;
  /** active flag : volatile is needed by multi threading */
  private volatile boolean active = true;
  /** stop signal */
  private CountDownLatch stopLatch = new CountDownLatch(1);

  public PollingThread(final long wait) {
    this.wait = wait;

    // This thread will die as JVM stops.
    this.setDaemon(false);

    // sets a low priority to this thread :
    setPriority(Thread.MIN_PRIORITY);
  }

  @Override
  public final void run() {
    if (DEBUG && log.isInfoEnabled()) {
      log.info("PollingThread[" + getName() + "].run() : enter");
    }

    for (; isActive();) {

      // calls handle method :
      if (DEBUG && log.isInfoEnabled()) {
        log.info("PollingThread[" + getName() + "].run() : calling handle()");
      }
      this.handle();

      // waits ...
      if (DEBUG && log.isInfoEnabled()) {
        log.info("PollingThread[" + getName() + "].run() : wait for " + (wait / 1000) + " seconds.");
      }
      try {
        Thread.sleep(wait);
      } catch (InterruptedException ie) {
        // thrown when Thread.interrupt() is sent by another thread
        if (DEBUG && log.isInfoEnabled()) {
          log.info("PollingThread[" + getName() + "].run() : interrupted.");
        }
        // force that thread to stop :
        setActive(false);
      }
      
    } // while active

    // thread will stop now :
    if (DEBUG && log.isInfoEnabled()) {
      log.info("PollingThread[" + getName() + "].run() : exit");
    }
    
    stopLatch.countDown();
  }

  /**
   * Must be implemented in child classes
   * 
   * called by thread every top time
   */
  public abstract void handle();

  public final boolean isActive() {
    return active;
  }

  public final void setActive(final boolean active) {
    this.active = active;
  }
  
  public final void stopAndWait() {
    try {
      this.interrupt();
      stopLatch.await();
    } catch (InterruptedException ex) {
      if (log.isDebugEnabled()) {
        log.debug("PollingThread[" + getName() + "].waitStopSignal() : interrupted.");
      }
    }
  }
}
