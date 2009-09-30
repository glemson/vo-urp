package org.ivoa.util.runner;

import org.apache.commons.logging.Log;

import org.ivoa.util.runner.process.ProcessContext;
import org.ivoa.util.runner.process.ProcessRunner;
import org.ivoa.util.runner.process.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.concurrent.CustomThreadPoolExecutor;
import org.ivoa.util.concurrent.FastSemaphore;
import org.ivoa.util.concurrent.GenericRunnable;
import org.ivoa.util.concurrent.ThreadExecutors;


/**
 * Job Management (queue & execution) on local machine
 *
 * @author laurent bourges (voparis)
 */
public final class LocalLauncher {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  protected static Log log = LogUtil.getLogger();
  /** initial capacity for queue */
  public static final int INITIAL_QUEUE_CAPACITY = 100;
  /** job ID generator (counter) */
  private static AtomicInteger JOBS_ID = new AtomicInteger(0);
  /** live job count */
  private static AtomicInteger JOBS_LIVE = new AtomicInteger(0);
  /** total queued job count */
  private static AtomicInteger JOBS_QUEUED = new AtomicInteger(0);
  /** total job count */
  private static AtomicInteger JOBS_TOTAL = new AtomicInteger(0);
  /** remove policy for queue : default automatic remove after job finished */
  private static boolean QUEUE_MANUAL_REMOVE_JOBS = false;
  /** queue semaphore */
  private static FastSemaphore QUEUE_SEM = new FastSemaphore(1);
  /** QUEUE for job management */
  private static Map<Long, RootContext> JOB_QUEUE = new LinkedHashMap<Long, RootContext>(
      INITIAL_QUEUE_CAPACITY);
  /** Job Listeners */
  private static Map<String, JobListener> JOB_LISTENER = new HashMap<String, JobListener>();
  /** Invalid executor type */
  public static final int ILLEGAL_STATE_ERROR_CODE = -1000;
  /** Use Persistence for Jobs */
  private static boolean USE_PERSISTENCE = false;
  /** limit of lines in ring buffer */
  public final static int MAX_LINES = 25;
  /** Use Persistence for Jobs */
  private static JobJPAManager jm = null;
  //~ Members ----------------------------------------------------------------------------------------------------------
  /** last total logged */
  private int lastTotal = -1;
  /** last live logged */
  private int lastLive = -1;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Forbidden Constructor
   */
  private LocalLauncher() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Prepare thread executors
   * @see ThreadExecutors#startExecutors()
   */
  public static void startUp() {
    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.startUp : enter");
    }

    ThreadExecutors.startExecutors();

    USE_PERSISTENCE = RuntimeConfiguration.getInstance().getBoolean("persistent.queue");
    if (USE_PERSISTENCE) {
      jm = JobJPAManager.getInstance();
    }

    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.startUp : exit");
    }
  }

  /**
   * Stop thread executors
   * @see ThreadExecutors#stopExecutors()
   */
  public static void shutdown() {
    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.shutdown : enter");
    }

    ThreadExecutors.stopExecutors();

    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.shutdown : exit");
    }
  }

  /**
   * Register an application plugin / listener (interface) at runtime
   * @param applicationName name of the managed application
   * @param listener job listener
   */
  public static void registerJobListener(final String applicationName, final JobListener listener) {
    log.error("registerJobListener : " + applicationName + " : " + listener);
    JOB_LISTENER.put(applicationName, listener);

    // preload pending tasks :
    if (USE_PERSISTENCE) {
      final List<RootContext> ctxList = jm.findContexts(applicationName, false);
      if (!JavaUtils.isEmpty(ctxList)) {

        for (RootContext ctx : ctxList) {
          // TODO : set writeLogFile (keep file path) :
          ctx.setRing(new RingBuffer(MAX_LINES, null));

          // initialize transient fields :
          for (RunContext child : ctx.getChildContexts()) {
            child.setRing(ctx.getRing());
          }

          queueJob(ctx);
        }
      }
    }

  }

  /**
   * Purge from the memory queue the finished jobs (must be called by a separate thread)
   *
   * @param delay time in milliseconds to wait after the job has finished before removing it from the queue
   */
  public static void purgeTerminated(final long delay) {
    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.purgeTerminated : enter");
    }

    int n = 0;
    final List<RootContext> list = getQueue();

    if (list != null) {
      if (log.isInfoEnabled()) {
        log.info("LocalLauncher.purgeTerminated : queue size : " + list.size());
      }

      final long now = System.currentTimeMillis();

      long duration;

      for (final RunContext job : list) {
        if ((job.getState() == RunState.STATE_FINISHED_ERROR) || (job.getState() == RunState.STATE_FINISHED_OK)) {
          duration = (now - job.getEndDate().getTime());

          if (duration > delay) {
            removeFromQueue(job.getId());
            n++;
          }
        }
      }
    }

    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.purgeTerminated : removed items : " + n);
    }
  }

  public static int getLiveJobs() {
    return JOBS_LIVE.get();
  }

  public static int getQueuedJobs() {
    return JOBS_QUEUED.get();
  }

  public static int getTotalJobs() {
    return JOBS_TOTAL.get();
  }

  /**
   * Defines the queue remove policy
   *
   * @param doManualRemove true if manual purge
   */
  public static void setQueueRemovePolicy(final boolean doManualRemove) {
    QUEUE_MANUAL_REMOVE_JOBS = doManualRemove;
  }

  /**
   * Logs the Launcher statistics
   */
  public void handle() {
    final int live = JOBS_LIVE.get();
    final int total = JOBS_TOTAL.get();

    if ((live > lastLive) || (total > lastTotal)) {
      // fast simple barrier :
      lastLive = live;
      lastTotal = total;

      if (log.isWarnEnabled()) {
        log.warn("LocalLauncher : Live jobs : " + live + " / Total jobs created : " + total);
      }
    }
  }

  /**
   * Create a ProcessContext with the given command and arguments
   *
   * @param appName application identifier
   * @param owner user name
   * @param workingDir working directory to use (null indicates current directory)
   * @param writeLogFile absolute file path to write STD OUT / ERR streams (null indicates not to use a file dump)
   *
   * @return created job context
   */
  public static RootContext prepareMainJob(final String appName, final String owner, final String workingDir, final String writeLogFile) {
    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareMainJob : enter");
    }

    final Long id = Long.valueOf(JOBS_ID.decrementAndGet());

    final RootContext runCtx = new RootContext(appName, id, workingDir);
    runCtx.setOwner(owner);
    runCtx.setRing(new RingBuffer(MAX_LINES, writeLogFile));

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareMainJob : exit : " + runCtx);
    }

    return runCtx;
  }

  /**
   * Create a ProcessContext with the given command and arguments
   *
   * @param parent root context of the given run context
   * @param name name of the operation
   * @param command unix command with arguments as an array
   *
   * @return created job context
   */
  public static RunContext prepareChildJob(final RootContext parent, final String name, final String[] command) {
    if (JavaUtils.isEmpty(command)) {
      throw new IllegalArgumentException("Invalid command parameter !");
    }

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareJob : enter");
    }

    final Long id = Long.valueOf(JOBS_ID.decrementAndGet());

    final ProcessContext runCtx = new ProcessContext(parent, name, id, command);

    // set pending state :
    runCtx.setState(RunState.STATE_PENDING);

    runCtx.setRing(parent.getRing());

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareJob : exit : " + runCtx);
    }

    return runCtx;
  }

  /**
   * Adds a job context in the queue and call the registered listener if job is accepted in the queue (pending).
   * The job will be executed by the Process Thread pool.
   * 
   * @see JobRunner
   * @see ThreadExecutors
   *
   * @param rootCtx root context to execute
   */
  public static void startJob(final RootContext rootCtx) {
    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.startJob : enter");
    }

    if (log.isInfoEnabled()) {
      log.info("LocalLauncher.startJob : starting job : " + rootCtx.shortString());
    }

    // set pending state :
    rootCtx.setState(RunState.STATE_PENDING);

    if (USE_PERSISTENCE) {
      jm.persist(rootCtx);
    }

    queueJob(rootCtx);

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.startJob : exit");
    }
  }

  /**
   * Add a pending job to the queue
   * @param rootCtx job to add
   */
  private static void queueJob(final RootContext rootCtx) {
    // Get the registered job listener :
    final JobListener listener = JOB_LISTENER.get(rootCtx.getName());

    if (listener == null) {
      throw new IllegalStateException("Job listener is undefined for the application : " + rootCtx.getName());
    }

    // uses the runner thread pool to run the job :
    // throws IllegalStateException if the job is not queued or the thread pool is down :
    final ThreadExecutors e = ThreadExecutors.getRunnerExecutor();

    // The executor is ready to accept new tasks :
    final Future future = e.submit(new JobRunner(e.getExecutor(), rootCtx, listener));

    // increment total counter :
    JOBS_TOTAL.incrementAndGet();

    // Here : job has been accepted and queued in ThreadExecutor (maybe already running) :

    // define the future associated to the root context :
    rootCtx.setFuture(future);

    // add in queue for monitoring :
    addInQueue(rootCtx);

    // call listener :
    listener.performJobEvent(rootCtx);
  }

  public static void killJob(final Long id) {
    final RunContext runCtx = LocalLauncher.getJob(id);
    if (runCtx != null) {
      try {
        if (runCtx instanceof RootContext) {
          // cancel the root context :
          final RootContext ctx = ((RootContext) runCtx);
          if (ctx.getState() == RunState.STATE_RUNNING) {
            final RunContext child = ctx.getCurrentChildContext();

            if (child != null) {
              ctx.setState(RunState.STATE_KILLED);
              child.kill();
            }
          }

        }

      } finally {
        // clear ring buffer :
        runCtx.close();
      }
    }
  }

  public static void cancelJob(final Long id) {
    final RunContext runCtx = LocalLauncher.getJob(id);
    if (runCtx != null) {
      try {
        if (runCtx instanceof RootContext) {
          // cancel the root context :
          final RootContext ctx = ((RootContext) runCtx);
          if (ctx.getState() == RunState.STATE_PENDING) {
            ctx.setState(RunState.STATE_CANCELLED);
            if (ctx.getFuture() != null) {
              // cancel a pending task :
              ctx.getFuture().cancel(true);
            }

            if (USE_PERSISTENCE) {
              jm.persist(runCtx);
            }
          }
        }

      } finally {
        // clear ring buffer :
        runCtx.close();
      }
    }
  }

  /**
   * Add a job context in the queue
   *
   * @param rootCtx job context
   */
  protected static void addInQueue(final RootContext rootCtx) {
    if (log.isInfoEnabled()) {
      log.info("LocalLauncher.addInQueue : job queued : " + rootCtx.shortString());
    }

    try {
      // semaphore is acquired to protect queue :
      QUEUE_SEM.acquire();

      JOB_QUEUE.put(rootCtx.getId(), rootCtx);
    } catch (final InterruptedException ie) {
      log.error("LocalLauncher.addInQueue : interrupted : ", ie);
    } finally {
      // semaphore is released :
      QUEUE_SEM.release();
    }

    // increment queue counter :
    JOBS_QUEUED.incrementAndGet();
  }

  /**
   * Remove a job context with the given identifier from the queue
   *
   * @param id job identifier
   */
  public static void removeFromQueue(final Long id) {
    if (log.isInfoEnabled()) {
      log.info("LocalLauncher.removeFromQueue : job to remove : " + id);
    }

    try {
      // semaphore is acquired to protect queue :
      QUEUE_SEM.acquire();

      final RunContext runCtx = JOB_QUEUE.remove(id);

      if (runCtx == null) {
        log.error("LocalLauncher.removeFromQueue : job not found in queue : " + id);
      } else {
        if (log.isInfoEnabled()) {
          log.info("LocalLauncher.removeFromQueue : job removed from queue : " + runCtx.shortString());
        }
      }
    } catch (final InterruptedException ie) {
      log.error("LocalLauncher.removeFromQueue : interrupted : ", ie);
    } finally {
      // semaphore is released :
      QUEUE_SEM.release();
    }
  }

  /**
   * Return a copy of the current queue (used to display its state)
   *
   * @return List of job present in the queue when this method is called
   */
  public static List<RootContext> getQueue() {
    try {
      // semaphore is acquired to protect queue :
      QUEUE_SEM.acquire();

      return new ArrayList<RootContext>(JOB_QUEUE.values());
    } catch (final InterruptedException ie) {
      log.error("LocalLauncher.getQueue : interrupted : ", ie);
    } finally {
      // semaphore is released :
      QUEUE_SEM.release();
    }

    return null;
  }

  /**
   * Return a job context for the given identifier
   *
   * @param id job identifier
   *
   * @return job context or null if not present
   */
  public static RunContext getJob(final Long id) {
    try {
      // semaphore is acquired to protect queue :
      QUEUE_SEM.acquire();

      return JOB_QUEUE.get(id);
    } catch (final InterruptedException ie) {
      log.error("LocalLauncher.getJob : interrupted : ", ie);
    } finally {
      // semaphore is released :
      QUEUE_SEM.release();
    }

    return null;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * This class implements Runnable to run a job submitted in the queue
   */
  private static final class JobRunner extends GenericRunnable {

    public final static int MAX_TASKS = 100;
    //~ Members --------------------------------------------------------------------------------------------------------
    /** thread pool running this job used to get its status (running, shutdown, terminated) */
    private final CustomThreadPoolExecutor executor;
    /** job context */
    private final RootContext rootCtx;
    /** job listener */
    private final JobListener listener;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    /**
     * Constructor for the given job context and listener
     * @param executorService thread pool running this job
     * @param rootCtx job context
     * @param listener job listener
     */
    protected JobRunner(final CustomThreadPoolExecutor executorService, final RootContext rootCtx, final JobListener listener) {
      this.executor = executorService;
      this.rootCtx = rootCtx;
      this.listener = listener;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    /**
     * This method uses the job listener for the running & finished events and use the ProcessRunner to execute the job
     */
    public final void run() {
      if (log.isDebugEnabled()) {
        log.debug("JobRunner.run : enter");
      }

      if (rootCtx.getState() != RunState.STATE_CANCELLED) {
        // increment live counter :
        JOBS_LIVE.incrementAndGet();

        RunState lastState = null;
        boolean ok = true;
        try {
          // set running state :
          rootCtx.setState(RunState.STATE_RUNNING);

          if (USE_PERSISTENCE) {
            jm.persist(rootCtx);
          }

          // call listener :
          listener.performJobEvent(rootCtx);

          // Execute the tasks here :
          int n = 0;
          RunContext child = null;

          while (ok && rootCtx.hasNext() && n < MAX_TASKS) {
            child = rootCtx.next();

            ok = false;

            executeTask(child);

            lastState = child.getState();

            // call listener :
            ok = listener.performTaskDone(rootCtx, child);

            if (!ok) {
              break;
            }

            if (USE_PERSISTENCE) {
              // persist state of child contexts :
              jm.persist(rootCtx);
            }

            // go forward in child contexts :
            rootCtx.goNext();
            n++;
          }

        } catch (RuntimeException re) {
          log.error("JobRunner.run : runtime exception : ", re);
          ok = false;
        } finally {

          rootCtx.getRing().add("Job '" + rootCtx.getName() + "' Ended.");

          // handle states :
          if (RunState.STATE_INTERRUPTED == lastState && this.executor.isShutdown()) {
            // interrupted due to thread pool shutdown :
            rootCtx.setState(RunState.STATE_INTERRUPTED);
          } else {
            if (rootCtx.getState() != RunState.STATE_CANCELLED && rootCtx.getState() != RunState.STATE_KILLED) {
              // set finished state :
              rootCtx.setState(ok ? RunState.STATE_FINISHED_OK : RunState.STATE_FINISHED_ERROR);
            }
          }

          // persist the context state anyway :
          if (USE_PERSISTENCE) {
            jm.persist(rootCtx);
          }

          // call listener :
          listener.performJobEvent(rootCtx);

          // remove job from queue :
          if (!QUEUE_MANUAL_REMOVE_JOBS) {
            removeFromQueue(rootCtx.getId());
          }
        }

        // decrement live counter :
        JOBS_LIVE.decrementAndGet();
      }

      if (log.isDebugEnabled()) {
        log.debug("JobRunner - thread.run : exit");
      }
    }

    /**
     * This method uses the job listener for the running & finished events and use the ProcessRunner to execute the job
     * @param runCtx context to execute
     */
    private final void executeTask(final RunContext runCtx) {
      if (log.isDebugEnabled()) {
        log.debug("JobRunner.executeTask : enter : " + runCtx.getId());
      }

      int status = ProcessRunner.STATUS_UNDEFINED;
      try {
        // set running state :
        runCtx.setState(RunState.STATE_RUNNING);

        // call listener :
        listener.performTaskEvent(runCtx.getParent(), runCtx);

        // starts program & waits for its end (and std threads) :
        // uses a ring buffer for stdout/stderr :

        if (runCtx instanceof ProcessContext) {
          status = ProcessRunner.execute((ProcessContext) runCtx);
        } else {
          status = ILLEGAL_STATE_ERROR_CODE;
        }

        if (log.isInfoEnabled()) {
          log.info("JobRunner.run : process return status : " + status);
        }

        // ring buffer is not synchronized because threads have finished their jobs in ProcessRunner.run(runCtx) :
        switch (status) {
          case ProcessRunner.STATUS_NORMAL:
            runCtx.getRing().add("Task '" + runCtx.getName() + "' Ended.");
            break;
          case ProcessRunner.STATUS_INTERRUPTED:
            runCtx.getRing().add(ProcessRunner.ERR_PREFIX, "Task Interrupted.");
            break;
          case ProcessRunner.STATUS_UNDEFINED:
          default:
            runCtx.getRing().add(ProcessRunner.ERR_PREFIX, "Task Ended with an error code : " + status + ".");
            break;
        }

      } finally {

        // set finished state :
        switch (status) {
          case ProcessRunner.STATUS_NORMAL:
            runCtx.setState(RunState.STATE_FINISHED_OK);
            break;
          case ProcessRunner.STATUS_INTERRUPTED:
            runCtx.setState(RunState.STATE_INTERRUPTED);
            break;
          case ProcessRunner.STATUS_UNDEFINED:
          default:
            runCtx.setState(RunState.STATE_FINISHED_ERROR);
            break;
        }

        // call listener :
        listener.performTaskEvent(runCtx.getParent(), runCtx);
      }

      if (log.isDebugEnabled()) {
        log.debug("JobRunner.executeTask : exit : " + runCtx.getId());
      }
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------

