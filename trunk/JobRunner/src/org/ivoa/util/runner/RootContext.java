package org.ivoa.util.runner;

import org.ivoa.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;


/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
public final class RootContext extends RunContext implements Iterator<RunContext> {

  /** future used to be able to cancel the job */
  private Future<?> future = null;
  /**
   * The user who owns this run
   */
  private String owner;
  /**
   * Process working directory
   */
  private final String workingDir;
  /**
   * Relative path to results of the job in either runner or archive
   */
  private String relativePath;
  /**
   * Child contexts
   */
  private final List<RunContext> childContexts = new ArrayList<RunContext>();
  /**
   * Current executed task position in the Child contexts
   */
  private int currentTask = 0;

  /**
   * Creates a new RunContext object
   *
   * @param applicationName application identifier
   * @param id job identifier
   * @param workingDir user's temporary working directory
   */
  public RootContext(final String applicationName, final Integer id,
                     final String workingDir) {
    super(null, applicationName, id);
    this.workingDir = workingDir;
  }

  /**
   * This method can be used to release resources
   */
  @Override
  public void close() {
    // clean up code :
    }

  public void cancel() {
    if (getState() == RunState.STATE_PENDING) {
      if (future != null) {
        setState(RunState.STATE_CANCELLED);
        // cancel a pending task :
        future.cancel(true);
      }
    }
  }

  /**
   * this method stops the execution of that context
   */
  @Override
  public void kill() {
    // maybe should traverse all contexts to disable their execution

    if (getState() == RunState.STATE_RUNNING) {
      final RunContext ctx = getCurrentChildContext();

      if (ctx != null) {
        setState(RunState.STATE_KILLED);
        ctx.kill();
      }
    }
  }

  /**
   * Simple toString representation : "job[id][state] duration ms."
   *
   * @return "job[id][state] duration ms."
   */
  @Override
  public String toString() {
    return "job [" + getId() + "][" + getState() + "] " +
        ((getDuration() > 0L) ? (" : " + getDuration() + " ms.") : "") +
        ((getChildContexts() != null)
        ? (CollectionUtils.toString(getChildContexts())) : "");
  }

  /**
   * Return the future associated with this root context
   * @return future associated with this root context
   */
  protected Future<?> getFuture() {
    return future;
  }

  /**
   * Define the future associated to the execution of this root context
   * @param pFuture future instance
   */
  protected void setFuture(Future<?> pFuture) {
    this.future = pFuture;
  }

  public String getApplicationName() {
    return getName();
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Returns the process working directory
   *
   * @return process working directory
   */
  @Override
  public String getWorkingDir() {
    return workingDir;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public List<RunContext> getChildContexts() {
    return childContexts;
  }

  public RunContext getCurrentChildContext() {
    if (currentTask > 0 && currentTask <= this.childContexts.size()) {
      return this.childContexts.get(currentTask - 1);
    }
    return null;
  }

  public void addChild(RunContext childContext) {
    this.childContexts.add(childContext);
  }

  public boolean hasNext() {
    return currentTask < this.childContexts.size();
  }

  public RunContext next() {
    return this.childContexts.get(currentTask++);
  }

  public void remove() {
    /* no-op */
  }
}
