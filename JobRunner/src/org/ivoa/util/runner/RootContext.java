package org.ivoa.util.runner;

import org.ivoa.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
@Entity
@Table(name = "root_context")
@DiscriminatorValue("RootContext")
@NamedQueries({
@NamedQuery(name = "RootContext.findPendingByName", query = "SELECT o FROM RootContext o WHERE o.state = :state and o.name = :name")
})
public final class RootContext extends RunContext implements Iterator<RunContext> {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface
   */
  private static final long serialVersionUID = 1L;
  //~ Members ----------------------------------------------------------------------------------------------------------
  /** future used to be able to cancel the job */
  @Transient
  private Future<?> future = null;
  /**
   * The user who owns this run (login)
   */
  @Basic(optional = false)
  @Column(name = "owner", nullable = false)
  private String owner;
  /**
   * Process working directory
   */
  @Basic(optional = false)
  @Column(name = "workingDir", nullable = false)
  private String workingDir;
  /**
   * Relative path to results of the job in either runner or archive
   */
  @Basic(optional = false)
  @Column(name = "relativePath", nullable = false)
  private String relativePath;
  /**
   * Child contexts (No cascade at all to have unary operation) TODO ??
   */
  @OrderBy(value = "id")
  @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parent")
  private final List<RunContext> childContexts = new ArrayList<RunContext>();
  /**
   * Current executed task position in the Child contexts
   */
  @Basic(optional = false)
  @Column(name = "currentTask", nullable = false)
  private int currentTask = 0;

  /**
   * Creates a new RunContext object for JPA
   */
  public RootContext() {
    super();
  }

  /**
   * Creates a new RunContext object
   *
   * @param applicationName application identifier
   * @param id job identifier
   * @param workingDir user's temporary working directory
   */
  public RootContext(final String applicationName, final Long id,
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
    return super.toString() + ((getChildContexts() != null)
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
  protected void setFuture(final Future<?> pFuture) {
    this.future = pFuture;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(final String owner) {
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

  public void setRelativePath(final String relativePath) {
    this.relativePath = relativePath;
  }

  public List<RunContext> getChildContexts() {
    return childContexts;
  }

  public RunContext getCurrentChildContext() {
    if (currentTask < this.childContexts.size()) {
      return this.childContexts.get(currentTask);
    }
    return null;
  }

  public void addChild(final RunContext childContext) {
    this.childContexts.add(childContext);
  }

  public boolean hasNext() {
    return currentTask < this.childContexts.size();
  }

  public RunContext next() {
    return this.childContexts.get(currentTask);
  }

  public void goNext() {
    currentTask++;
  }

  public void remove() {
    /* no-op */
  }
}
