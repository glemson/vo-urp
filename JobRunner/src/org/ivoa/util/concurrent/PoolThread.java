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

  /**
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();

  //~ Constructors -----------------------------------------------------------------------------------------------------
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

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Log and Interrupt this thread.
   */
  @Override
  public void interrupt() {
    if (logB.isInfoEnabled()) {
      logB.info(getName() + " : interrupt");
    }
    super.interrupt();
  }

  /**
   * Log and start this thread.
   */
  @Override
  public synchronized void start() {
    if (logB.isInfoEnabled()) {
      logB.info(getName() + " : start");
    }
    super.start();
  }

  /**
   * Run method overridden to add logs and clean up
   */
  @Override
  public void run() {
    if (logB.isInfoEnabled()) {
      logB.info(getName() + " : before run() : ");
    }
    try {
      super.run();
    } finally {
      logB.error("THREAD STOPPPING !!");
      
      if (logB.isInfoEnabled()) {
        logB.info(getName() + " : after run() : ");
      }
      // Free any ThreadLocal value :
      ThreadLocalUtils.clearThreadLocals();
    }

  }

  // ~ End of file
  // --------------------------------------------------------------------------------------------------------
}
