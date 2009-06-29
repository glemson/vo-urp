package org.ivoa.util.concurrent;

import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * This class extends {@link java.lang.Thread} to add the logging system and logs on interrupt and
 * start methods
 * 
 * @see CustomThreadFactory
 * @see org.ivoa.bean.LogSupport
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class PoolThread extends Thread {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** Dev Logger for this class and subclasses */
  private final Log logD = LogUtil.getLoggerDev();

  // ~ End of file
  // --------------------------------------------------------------------------------------------------------

  /**
   * Allocates a new <code>Thread</code> object.
   * 
   * @param target the object whose <code>run</code> method is called.
   * @param name the name of the new thread.
   * @see java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
   */
  public PoolThread(final Runnable target, final String name) {
    super(target, name);
  }

  /**
   * Log and Interrupt this thread.
   */
  @Override
  public void interrupt() {
    if (logD.isInfoEnabled()) {
      logD.info(getName() + " : interrupt");
    }
    super.interrupt();
  }

  /**
   * Log and start this thread.
   */
  @Override
  public synchronized void start() {
    if (logD.isInfoEnabled()) {
      logD.info(getName() + " : start");
    }
    super.start();
  }

  /**
   * Run method overriden to add logs and clean up
   */
  @Override
  public void run() {
    if (logD.isInfoEnabled()) {
      logD.info(getName() + " : before run() : ");
    }
    try {
      super.run();
    } finally {
      if (logD.isInfoEnabled()) {
        logD.info(getName() + " : after run() : ");
      }
      // Free any ThreadLocal value :
      ThreadLocalUtils.clearThreadLocals();
    }

  }
}
