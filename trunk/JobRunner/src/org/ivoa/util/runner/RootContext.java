package org.ivoa.util.runner;

import org.ivoa.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
public final class RootContext extends RunContext implements Iterator<RunContext> {
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
     * @param id
     *            job identifier
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

  /**
   * this method stops the execution of that context
   */
  @Override
  public void kill() {
    // maybe should traverse all contexts to disable their execution

    // TODO : cancel a pending task
    final RunContext ctx = getCurrentChildContext();

    if (ctx != null) {
      ctx.kill();
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
