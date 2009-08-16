package org.ivoa.util.runner;

import org.apache.commons.logging.Log;

import org.ivoa.util.runner.process.ProcessContext;
import org.ivoa.util.runner.process.ProcessRunner;
import org.ivoa.util.runner.process.RingBuffer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.ivoa.util.LogUtil;
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
  protected static final Log log = LogUtil.getLogger();

  /** initial capacity for queue */
  public static final int INITIAL_QUEUE_CAPACITY = 100;

  /** job ID generator (counter) */
  private static final AtomicInteger JOBS_ID = new AtomicInteger(0);

  /** live job count */
  private static final AtomicInteger JOBS_LIVE = new AtomicInteger(0);

  /** total queued job count */
  private static final AtomicInteger JOBS_QUEUED = new AtomicInteger(0);

  /** total job count */
  private static final AtomicInteger JOBS_TOTAL = new AtomicInteger(0);

  /** remove policy for queue : default automatic remove after job finished */
  private static boolean QUEUE_MANUAL_REMOVE_JOBS = false;

  /** queue semaphore */
  private static final FastSemaphore QUEUE_SEM = new FastSemaphore(1);

  /** QUEUE for job management */
  private static final Map<Integer, RootContext> JOB_QUEUE = new LinkedHashMap<Integer, RootContext>(
    INITIAL_QUEUE_CAPACITY);

  // ~ ----
  public static final int ILLEGAL_STATE_ERROR_CODE = 1;
  
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
   * Purge from the queue the finished jobs (must be called by a separate thread)
   *
   * @param delay time in milliseconds to wait after the job has finished before removing it from the queue
   */
  public static void purgeTerminated(final long delay) {
    if (log.isWarnEnabled()) {
      log.warn("LocalLauncher.purgeTerminated : enter");
    }

    int                    n    = 0;
    final List<RootContext> list = getQueue();

    if (list != null) {
      if (log.isInfoEnabled()) {
        log.info("LocalLauncher.purgeTerminated : queue size : " + list.size());
      }

      final long now      = System.currentTimeMillis();

      long       duration;

      for (final RunContext job : list) {
        if ((job.getState() == RunState.STATE_FINISHED_ERROR) || (job.getState() == RunState.STATE_FINISHED_OK)) {
          duration = (now - job.getEndDate());

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
    final int live        = JOBS_LIVE.get();
    final int total       = JOBS_TOTAL.get();

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
   * @param workingDir working directory to use (null indicates current directory)
   * @param bufferLines number of lines to keep in STD OUT / ERR buffer
   * @param writeLogFile absolute file path to write STD OUT / ERR streams (null indicates not to use a file dump)
   *
   * @return created job context
   */
  public static RootContext prepareMainJob(final String appName, final String owner, final String workingDir, final int bufferLines,
                                          final String writeLogFile) {
    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareMainJob : enter");
    }

    final Integer        id     = Integer.valueOf(JOBS_ID.incrementAndGet());

    final RootContext runCtx = new RootContext(appName, id, workingDir);
    runCtx.setOwner(owner);
    runCtx.setRing(new RingBuffer(bufferLines, writeLogFile));

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareMainJob : exit : " + runCtx);
    }

    return runCtx;
  }

  /**
   * Create a ProcessContext with the given command and arguments
   *
   * @param name name of the operation
   * @param command unix command with arguments as an array
   *
   * @return created job context
   */
  public static RunContext prepareChildJob(final RootContext parent, final String name, final String[] command) {
    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareJob : enter");
    }

    final Integer        id     = Integer.valueOf(JOBS_ID.incrementAndGet());

    final ProcessContext runCtx = new ProcessContext(parent, name, id, command);

    runCtx.setRing(parent.getRing());

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.prepareJob : exit : " + runCtx);
    }

    return runCtx;
  }  
  
  /**
   * Adds a job context in the queue and call the listener if job is pending.
   * The job will be executed by the Process Thread pool.
   * 
   * @see JobRunner
   * @see ThreadExecutors
   *
   * @param rootCtx root context to execute
   * @param listener job listener
   */
  public static void startJob(final RootContext rootCtx, final JobListener listener) {
    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.startJob : enter");
    }

    if (log.isInfoEnabled()) {
      log.info("LocalLauncher.startJob : starting job ...");
    }

    // set pending state :
    rootCtx.setState(RunState.STATE_PENDING);

    // uses the runner thread pool to run the pdr process :
    // throws IllegalStateException if job not queued :
    ThreadExecutors.getRunnerExecutor().execute(new JobRunner(rootCtx, listener));

    // Here : job has been accepted and queued in ThreadExecutor (maybe already running) :

    // add in queue for monitoring :
    addInQueue(rootCtx);

    // increment total counter :
    JOBS_TOTAL.incrementAndGet();

    // call listener :
    listener.performJobEvent(rootCtx);

    if (log.isDebugEnabled()) {
      log.debug("LocalLauncher.startJob : exit");
    }
  }

  /**
   * Stop a job in the queue (not implemented)
   *
   * @param runCtx job context
   */
  public static void stopJob(final RunContext runCtx) {
  }

  /**
   * Add a job context in the queue
   *
   * @param rootCtx job context
   */
  protected static void addInQueue(final RootContext rootCtx) {
    if (log.isInfoEnabled()) {
      log.info("LocalLauncher.addInQueue : job queued : " + rootCtx);
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
  public static void removeFromQueue(final Integer id) {
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
          log.info("LocalLauncher.removeFromQueue : job removed from queue : " + runCtx);
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
  public static RunContext getJob(final Integer id) {
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
    //~ Members --------------------------------------------------------------------------------------------------------

    /** job context */
    private final RootContext rootCtx;
    /** job listener */
    private final JobListener listener;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    /**
     * Constructor for the given job context and listener
     * @param rootCtx job context
     * @param listener job listener
     */
    protected JobRunner(final RootContext rootCtx, final JobListener listener) {
      this.rootCtx = rootCtx;
      this.listener = listener;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * This method uses the job listener for the running & finished events and use the ProcessRunner to execute the job
     */
    public final void run() {
      if (log.isDebugEnabled()) {
        log.debug("JobRunner - thread.run : enter");
      }

      // increment live counter :
      JOBS_LIVE.incrementAndGet();

      boolean ok = true;
      try {
        // set running state :
    	  rootCtx.setState(RunState.STATE_RUNNING);

        // call listener :
        listener.performJobEvent(rootCtx);


        // Execute the tasks here :
        int n = 0;
        int maxn = 100;
        RunContext child = null;

        while(ok && rootCtx.hasNext() && n < maxn) {
        	child = rootCtx.next();
            executeTask(child);
        	ok = listener.performTaskDone(rootCtx, child);
        	n++;
        }

      } finally {
    	  
    	  rootCtx.getRing().add("Job '"+rootCtx.getName()+"'Ended.");
    	  
        // set finished state :
    	  rootCtx.setState(ok ? RunState.STATE_FINISHED_OK : RunState.STATE_FINISHED_ERROR);

        // call listener :
        listener.performJobEvent(rootCtx);

        // remove job from queue :
        if (! QUEUE_MANUAL_REMOVE_JOBS) {
          removeFromQueue(rootCtx.getId());
        }
      }

      // decrement live counter :
      JOBS_LIVE.decrementAndGet();

      if (log.isDebugEnabled()) {
        log.debug("JobRunner - thread.run : exit");
      }
    }
	  
	  /**
	   * This method uses the job listener for the running & finished events and use the ProcessRunner to execute the job
	   * @return true if the execution was successful
	   */
	  private final void executeTask(final RunContext runCtx) {
	    if (log.isDebugEnabled()) {
	      log.debug("JobRunner.executeTask : enter : " + runCtx.getId());
	    }
	    
	    int status = -1;
	    try {
	      // set running state :
	      runCtx.setState(RunState.STATE_RUNNING);
	
	      // call listener :
	      listener.performTaskEvent(runCtx.getParent(), runCtx);
	
	      // starts program & waits for its end (and std threads) :
	      // uses a ring buffer for stdout/stderr :
	      
	      if(runCtx instanceof ProcessContext)
	    	  status = ProcessRunner.execute((ProcessContext)runCtx);
	      else
	    	  status = ILLEGAL_STATE_ERROR_CODE;
	    		  
	      if (log.isInfoEnabled()) {
	        log.info("JobRunner.run : process return status : " + status);
	      }
	
	      // ring buffer is not synchronized because threads have finished their jobs in ProcessRunner.run(runCtx) :
	      if (status == 0) {
	        runCtx.getRing().add("Task '"+runCtx.getName()+"' Ended.");
	      } else {
	        runCtx.getRing().add(ProcessRunner.ERR_PREFIX, "Task Ended with a failure exit code : " + status + ".");
	      }
	    } finally {
	      // set finished state :
	      runCtx.setState((status == 0) ? RunState.STATE_FINISHED_OK : RunState.STATE_FINISHED_ERROR);
	
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
