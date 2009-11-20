package org.ivoa.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;


/**
 * Custom ThreadPoolExecutor to add extensions
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CustomThreadPoolExecutor extends ThreadPoolExecutor {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /**
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();
  //~ Members ----------------------------------------------------------------------------------------------------------
  /** thread pool name */
  private final String name;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Single constructor allowed
   * 
   * @param pPoolName thread pool name
   * @param corePoolSize the number of threads to keep in the
   * pool, even if they are idle.
   * @param maximumPoolSize the maximum number of threads to allow in the
   * pool.
   * @param keepAliveTime when the number of threads is greater than
   * the core, this is the maximum time that excess idle threads
   * will wait for new tasks before terminating.
   * @param unit the time unit for the keepAliveTime
   * argument.
   * @param workQueue the queue to use for holding tasks before they
   * are executed. This queue will hold only the <tt>Runnable</tt>
   * tasks submitted by the <tt>execute</tt> method.
   * @param threadFactory the factory to use when the executor
   * creates a new thread.
   */
  public CustomThreadPoolExecutor(final String pPoolName,
                                  final int corePoolSize,
                                  final int maximumPoolSize,
                                  final long keepAliveTime,
                                  final TimeUnit unit,
                                  final BlockingQueue<Runnable> workQueue,
                                  final ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory);
    this.name = pPoolName;
  }

  /**
   * Return the thread pool name
   * @return thread pool name
   */
  public final String getPoolName() {
    return this.name;
  }

  /**
   * Called before a task is run
   * @param t thread used to run the task
   * @param r runnable task
   */
  @Override
  protected void beforeExecute(final Thread t, final Runnable r) {
    logB.error(this.name + ".beforeExecute : runnable : " + r);
  }

  /**
   * Called after the task has completed
   * @param r the runnable that has completed.
   * @param th the exception that caused termination, or null if
   * execution completed normally.
   */
  @Override
  protected void afterExecute(final Runnable r, final Throwable th) {
    if (th != null) {
      logB.error(this.name + ".afterExecute : uncaught exception : ", th);
    }
    logB.error(this.name + ".afterExecute : runnable : " + r);
  }

  /**
   * Method invoked when the Executor has terminated.  Default
   * implementation does nothing. Note: To properly nest multiple
   * overridings, subclasses should generally invoke
   * <tt>super.terminated</tt> within this method.
   */
  @Override
  protected void terminated() {
    logB.error(this.name + ".terminated.");
  }

  /*
  private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<Runnable>());

  public List<Runnable> getCancelledTasks() {
  if (!exec.isTerminated())
  throw new IllegalStateException(...);
  return new ArrayList<Runnable>(tasksCancelledAtShutdown);
  }

  public void execute(final Runnable runnable) {
  exec.execute(new Runnable() {
  public void run() {
  try {
  runnable.run();
  } finally {
  if (isShutdown()
  && Thread.currentThread().isInterrupted())
  tasksCancelledAtShutdown.add(runnable);
  }
  }
  });
  }
   */
}
