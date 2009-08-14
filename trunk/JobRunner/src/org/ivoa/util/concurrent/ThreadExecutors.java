package org.ivoa.util.concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.JavaUtils;

/**
 * Thread Pools Utilities : this class simplifies the thread pool management with Java 1.5+. This
 * class maintains two thread pools :
 * <ul>
 * <li>generic thread pool : many small tasks (no queue limit, many threads)</li>
 * <li>process thread pool : long tasks (no queue limit, few threads)</li>
 * </ul>
 * 
 * @see ThreadPoolExecutor
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThreadExecutors extends LogSupport {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** default single thread pool name */
  public static final String DEFAULT_SINGLE_THREAD_POOL = "DefaultSinglePool";

  /** generic thread pool name */
  public static final String GENERIC_THREAD_POOL = "GenericPool";

  /** process thread pool name */
  public static final String PROCESS_THREAD_POOL = "ProcessPool";
  
  /** Generic thread Pool : idle thread keep alive before kill : 120s */
  public static final long GENERIC_THREAD_KEEP_ALIVE = 120L;

  /** Process thread pool : maximum threads : 3 */
  public static final int PROCESS_THREAD_MAX = 3;

  /** Generic thread pool : minimum threads : 7 */
  public static final int GENERIC_THREAD_MIN = (2 * PROCESS_THREAD_MAX) + 1;

  /** Generic thread pool : maximum threads : 17 */
  public static final int GENERIC_THREAD_MAX = (2 * PROCESS_THREAD_MAX) + 5;

  /** generic thread pool singleton */
  private static volatile ThreadExecutors genericExecutor;

  /** processRunner thread pool singleton */
  private static volatile ThreadExecutors runnerExecutor;

  /** single thread pool singletons : used to shutdown them */
  private static volatile Map<String, ThreadExecutors> singleExecutors = null;

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /** wrapped Java 5 Thread pool executor */
  private final ThreadPoolExecutor threadExecutor;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------

  /**
   * Constructor with the given thread pool executor
   * 
   * @param executor wrapped thread pool
   */
  protected ThreadExecutors(final ThreadPoolExecutor executor) {
    threadExecutor = executor;

    if (logB.isInfoEnabled()) {
      logB.info("ThreadExecutors.new : creating a new thread pool : " + getPoolName());
    }

    // creates now core threads (min threads) :
    executor.prestartAllCoreThreads();
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------

  /**
   * Calling thread sleeps for the given lapse delay
   * 
   * @param lapse delay in ms
   * @return true if thread awaken normally, false if interrupted
   */
  public static boolean sleep(final long lapse) {
    try {
      Thread.sleep(lapse);

      return true;
    } catch (final InterruptedException ie) {
      if (logB.isDebugEnabled()) {
        logB.debug(Thread.currentThread().getName() + " sleep : interrupted : ", ie);
      }
    }

    return false;
  }

  /**
   * Prepare the default single thread-pool to be ready (preallocate threads)
   * 
   * @see #getSingleExecutor(String)
   * @see #getGenericExecutor()
   */
  public static void startExecutors() {
    getSingleExecutor(DEFAULT_SINGLE_THREAD_POOL);
    /*
     * getGenericExecutor();
     */
  }

  /**
   * Prepare the singleExecutors map
   * @param doCreate flag to indicate to create the map if null
   * @return singleExecutors map
   */
  private final static Map<String, ThreadExecutors> getSingleExecutors(final boolean doCreate) {
    Map<String, ThreadExecutors> m = singleExecutors;
    if (doCreate) {
      while (m == null) {
        singleExecutors = new ConcurrentHashMap<String, ThreadExecutors>();

        // volatile & thread safety :
        m = singleExecutors;
      }
    }
    return m;
  }

  /**
   * Free the thread pools and stop the running tasks at this time
   * 
   * @see #stop()
   */
  public static void stopExecutors() {
    final Map<String, ThreadExecutors> m = getSingleExecutors(false);
    if (!JavaUtils.isEmpty(m)) {
      for (final Iterator<ThreadExecutors> it = m.values().iterator(); it.hasNext();) {
        it.next().stop();
        it.remove();
      }
    }

    if (runnerExecutor != null) {
      runnerExecutor.stop();
      runnerExecutor = null;
    }

    if (genericExecutor != null) {
      genericExecutor.stop();
      genericExecutor = null;
    }
  }

  /**
   * Return the generic thread pool or create it (lazy)
   * 
   * @see #newCachedThreadPool(int, int, ThreadFactory)
   * @return generic thread pool
   */
  public static ThreadExecutors getGenericExecutor() {
    if (genericExecutor == null) {
      genericExecutor = new ThreadExecutors(newCachedThreadPool(GENERIC_THREAD_MIN,
          GENERIC_THREAD_MAX, new CustomThreadFactory(GENERIC_THREAD_POOL)));
    }

    return genericExecutor;
  }

  /**
   * Return the process thread pool or create it (lazy)
   *
   * @see #newFixedThreadPool(int, ThreadFactory)
   *
   * @return process thread pool
   */
  public static ThreadExecutors getRunnerExecutor() {
    if (runnerExecutor == null) {
      runnerExecutor = new ThreadExecutors(
        newFixedThreadPool(PROCESS_THREAD_MAX, new CustomThreadFactory(PROCESS_THREAD_POOL)));
    }

    return runnerExecutor;
  }
  
  /**
   * Return the single-thread pool or create it (lazy) for the given name
   * 
   * @param name key or name of the single-thread pool
   * @see #newFixedThreadPool(int, ThreadFactory)
   * @return process thread pool
   */
  public static ThreadExecutors getSingleExecutor(final String name) {
    final Map<String, ThreadExecutors> m = getSingleExecutors(true);

    ThreadExecutors e = m.get(name);
    if (e == null) {
      e = new ThreadExecutors(newFixedThreadPool(1, new CustomThreadFactory(name)));

      final ThreadExecutors old = m.put(name, e);
      if (old != null) {
        old.stop();
      }
    }

    return e;
  }

  /**
   * Creates a thread pool that reuses a fixed set of threads operating off a shared unbounded
   * queue, using the provided ThreadFactory to create new threads when needed.
   * 
   * @param nThreads the number of threads in the pool
   * @param threadFactory the factory to use when creating new threads
   * @return the newly created thread pool
   */
  protected static ThreadPoolExecutor newFixedThreadPool(final int nThreads,
      final ThreadFactory threadFactory) {
    return (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, threadFactory);
  }

  /**
   * Creates a thread pool that creates new threads as needed, but will reuse previously constructed
   * threads when they are available, and uses the provided ThreadFactory to create new threads when
   * needed.
   * 
   * @param minThreads the number of threads to keep in the pool, even if they are idle.
   * @param nThreads the maximum number of threads to allow in the pool.
   * @param threadFactory the factory to use when creating new threads
   * @return the newly created thread pool
   */
  protected static ThreadPoolExecutor newCachedThreadPool(final int minThreads, final int nThreads,
      final ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(minThreads, nThreads, GENERIC_THREAD_KEEP_ALIVE,
        TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
  }

  /**
   * Executes the given command at some time in the future. The command may execute in a new thread,
   * in a pooled thread, or in the calling thread, at the discretion of the <tt>Executor</tt>
   * implementation.
   * 
   * @see ThreadPoolExecutor#execute(Runnable)
   * @param job the runnable task
   * @throws IllegalStateException if this task cannot be accepted for execution.
   */
  public void execute(final Runnable job) {
    if (logB.isDebugEnabled()) {
      logB.debug("ThreadExecutors.execute : execute job in pool : " + getPoolName() + " = " + job);
    }
    try {
      threadExecutor.execute(job);
    } catch (final RejectedExecutionException ree) {
      throw new IllegalStateException("unable to queue the job !", ree);
    }
  }

  /**
   * Submits a Runnable task for execution and returns a Future representing that task.
   * 
   * @see ThreadPoolExecutor#submit(Runnable)
   * @param job the task to submit
   * @return a Future representing pending completion of the task, and whose <tt>get()</tt> method
   *         will return <tt>null</tt> upon completion.
   * @throws IllegalStateException if task cannot be scheduled for execution
   */
  public Future<?> submit(final Runnable job) {
    if (logB.isDebugEnabled()) {
      logB.debug("ThreadExecutors.submit : submit job in pool : " + getPoolName() + " = " + job);
    }
    try {
      return threadExecutor.submit(job);
    } catch (final RejectedExecutionException ree) {
      throw new IllegalStateException("unable to queue the job !", ree);
    }
  }

  /**
   * Submits a Callable task for execution and returns a Future representing that task.
   * 
   * @see ThreadPoolExecutor#submit(Callable)
   * @param <T> result type
   * @param job the task to submit
   * @return a Future representing pending completion of the task, and whose <tt>get()</tt> method
   *         will return <tt>null</tt> upon completion.
   * @throws IllegalStateException if task cannot be scheduled for execution
   */
  public <T> Future<T> submit(final Callable<T> job) {
    if (logB.isDebugEnabled()) {
      logB.debug("ThreadExecutors.submit : submit job in pool : " + getPoolName() + " = " + job);
    }
    try {
      return threadExecutor.submit(job);
    } catch (final RejectedExecutionException ree) {
      throw new IllegalStateException("unable to queue the job !", ree);
    }
  }

  /**
   * Shutdown this thread pool now
   * 
   * @see ThreadPoolExecutor#shutdownNow()
   */
  protected void stop() {
    if (logB.isInfoEnabled()) {
      logB.info("ThreadExecutors.stop : starting shutdown now : " + getPoolName());
    }
    threadExecutor.shutdown();
    /* threadExecutor.shutdownNow(); */
  }

  /**
   * Return the thread pool name defined by the CustomThreadFactory
   * @return thread pool name
   */
  private final String getPoolName() {
    return ((CustomThreadFactory) threadExecutor.getThreadFactory()).getName();
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
