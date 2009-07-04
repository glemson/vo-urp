package org.ivoa.util;

import org.apache.commons.logging.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Simple Looping Thread that calls the handle() method then waits some time and loops till Thread.interrupt() sent
 * by another thread
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class PollingThread extends Thread {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** DEBUG mode */
  private static final boolean DEBUG = false;
  /**
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** wait time in milliseconds. */
  private long wait;
  /** active flag : volatile is needed by multi threading */
  private volatile boolean active = true;
  /** stop signal */
  private CountDownLatch stopLatch = new CountDownLatch(1);

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Creates a new PollingThread object
   *
   * @param pWait time to sleep
   */
  public PollingThread(final long pWait) {
    this.wait = pWait;

    // This thread will die as JVM stops.
    this.setDaemon(false);

    // sets a low priority to this thread :
    setPriority(Thread.MIN_PRIORITY);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * TODO : Method Description
   */
  @Override
  public final void run() {
    if (logB.isWarnEnabled()) {
      logB.warn("PollingThread[" + getName() + "].run() : enter");
    }

    for (; isActive();) {
      // calls handle method :
      if (DEBUG && logB.isInfoEnabled()) {
        logB.info("PollingThread[" + getName() + "].run() : calling handle()");
      }

      this.handle();

      // waits ...
      if (DEBUG && logB.isInfoEnabled()) {
        logB.info("PollingThread[" + getName() + "].run() : wait for " + (wait / 1000) + " seconds.");
      }

      try {
        Thread.sleep(this.wait);
      } catch (final InterruptedException ie) {
        // thrown when Thread.interrupt() is sent by another thread
        if (DEBUG && logB.isInfoEnabled()) {
          logB.info("PollingThread[" + getName() + "].run() : interrupted.");
        }

        // force that thread to stop :
        setActive(false);
      }
    } // while active

    this.stopLatch.countDown();

    // thread will stop now :
    if (logB.isWarnEnabled()) {
      logB.warn("PollingThread[" + getName() + "].run() : exit");
    }
  }

  /**
   * Must be implemented in child classes  called by thread every top time
   */
  public abstract void handle();

  /**
   * Return the active flag
   *
   * @return active flag
   */
  public final boolean isActive() {
    return this.active;
  }

  /**
   * Set the active flag
   *
   * @param pActive active flag
   */
  public final void setActive(final boolean pActive) {
    this.active = pActive;
  }

  /**
   * TODO : Method Description
   */
  public final void stopAndWait() {
    if (logB.isWarnEnabled()) {
      logB.warn("PollingThread[" + getName() + "].stopAndWait : enter");
    }

    try {
      this.interrupt();
      this.stopLatch.await();
    } catch (final InterruptedException ie) {
      if (logB.isWarnEnabled()) {
        logB.warn("PollingThread[" + getName() + "].stopAndWait : interrupted.", ie);
      }
    }
    if (logB.isWarnEnabled()) {
      logB.warn("PollingThread[" + getName() + "].stopAndWait : exit");
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
