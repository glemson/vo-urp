package org.ivoa.util.runner;

import java.util.Date;
import org.ivoa.util.TypeWrapper;
import org.ivoa.util.runner.process.RingBuffer;


/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
public class RunContext {

  /**
   * Job identifier
   */
  private final Integer id;
  /**
   * Root Context reference
   */
  private final RootContext parent;
  /**
   * Name of this task (useful to process task events)
   */
  private final String name;
  /**
   * Job creation date
   */
  private final long creationDate;
  /**
   * Job queue date
   */
  private long queueDate = 0L;
  /**
   * Job run date
   */
  private long runDate = 0L;
  /**
   * Job end date
   */
  private long endDate = 0L;
  /**
   * Job duration
   */
  private long duration = 0L;
  /**
   * Job state
   */
  private RunState state;
  /**
   * Ring Buffer for logs
   */
  private RingBuffer ring = null;

  /**
   * Creates a new RunContext object
   *
   * @param parent root context
   * @param name operation name
   * @param id job identifier
   */
  public RunContext(final RootContext parent, final String name, final Integer id) {
    this.parent = parent;
    this.id = id;
    this.name = name;
    // init :
    this.state = RunState.STATE_UNKNOWN;
    this.creationDate = System.currentTimeMillis();

    if (parent != null) {
      parent.addChild(this);
    }
  }

  /**
   * This method can be used to release resources : clear the ring buffer
   */
  public void close() {
    if (this.ring != null) {
      this.ring.close();
    }
  }

  /**
   * this method stops the execution of that context
   */
  public void kill() {
  }

  /**
   * Returns the process working directory
   *
   * @return process working directory
   */
  public String getWorkingDir() {
    return (getParent() != null) ? getParent().getWorkingDir() : null;
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
        " - work dir : " + getWorkingDir();
  }

  public RootContext getParent() {
    return parent;
  }

  /**
   * Returns the job identifier
   *
   * @return identifier
   */
  public final Integer getId() {
    return id;
  }

  /**
   * Returns the job state
   *
   * @return job state
   */
  public final RunState getState() {
    return state;
  }

  /**
   * Defines the job state and corresponding date
   *
   * @param state to set
   */
  protected final void setState(final RunState state) {
    this.state = state;

    switch (state) {
      case STATE_PENDING:
        setQueueDate(System.currentTimeMillis());

        break;

      case STATE_RUNNING:
        setRunDate(System.currentTimeMillis());

        break;

      case STATE_CANCELLED:
      case STATE_KILLED:
      case STATE_FINISHED_ERROR:
      case STATE_FINISHED_OK:
        setEndDate(System.currentTimeMillis());

        break;

      default:
    }
  }

  public boolean isRunning() {
    return getState() == RunState.STATE_RUNNING;
  }

  public boolean isPending() {
    return getState() == RunState.STATE_PENDING;
  }

  public long getCreationDate() {
    return creationDate;
  }

  public String getCreationDateFormatted() {
    return TypeWrapper.getInternationalFormat(creationDate);
  }

  /**
   * Returns the job queue date
   *
   * @return job queue date
   */
  public final long getQueueDate() {
    return queueDate;
  }

  /**
   * Defines the job queue date
   *
   * @param queueDate date to set
   */
  private final void setQueueDate(final long queueDate) {
    this.queueDate = queueDate;
  }

  public String getQueueDateFormatted() {
    if (queueDate == 0L) {
      return "-";
    }
    return TypeWrapper.getInternationalFormat(queueDate);
  }

  /**
   * Returns the job run date
   *
   * @return job run date
   */
  public final long getRunDate() {
    return runDate;
  }

  /**
   * Defines the job run date
   *
   * @param runDate run date to set
   */
  private final void setRunDate(final long runDate) {
    this.runDate = runDate;
  }

  public String getRunDateFormatted() {
    if (runDate == 0L) {
      return "-";
    }
    return TypeWrapper.getInternationalFormat(runDate);
  }

  /**
   * Returns the job end date
   *
   * @return job end date
   */
  public final long getEndDate() {
    return endDate;
  }

  /**
   * Defines the job end date
   *
   * @param endDate date to set
   */
  private final void setEndDate(final long endDate) {
    this.endDate = endDate;
  }

  public String getEndDateFormatted() {
    if (endDate == 0L) {
      return "-";
    }
    return TypeWrapper.getInternationalFormat(endDate);
  }

  /**
   * Returns the job duration in ms
   *
   * @return job duration
   */
  public final long getDuration() {
    return duration;
  }

  /**
   * Defines the job duration in ms
   *
   * @param duration to set
   */
  public final void setDuration(final long duration) {
    this.duration = duration;
  }

  /**
   * Returns the ring buffer
   *
   * @return ring buffer
   */
  public RingBuffer getRing() {
    return ring;
  }

  /**
   * Defines the ring buffer
   *
   * @param ring buffer to set
   */
  public void setRing(final RingBuffer ring) {
    this.ring = ring;
  }

  public String getName() {
    return name;
  }
}
