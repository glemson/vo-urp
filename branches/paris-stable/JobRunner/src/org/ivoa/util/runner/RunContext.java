package org.ivoa.util.runner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.ivoa.util.TypeWrapper;
import org.ivoa.util.runner.process.RingBuffer;
import org.vourp.runner.model.LegacyApp;
import org.vourp.runner.model.ParameterDeclaration;


/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
@Entity
@Table(name = "run_context")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
@DiscriminatorValue("RunContext")
@NamedQueries({@NamedQuery(name = "RunContext.findById", query = "SELECT o FROM RunContext o WHERE o.id = :id"),
@NamedQuery(name = "RunContext.findByName", query = "SELECT o FROM RunContext o WHERE o.name = :name")
})
public class RunContext implements Serializable, Cloneable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface
   */
  private static final long serialVersionUID = 1L;
  //~ Members ----------------------------------------------------------------------------------------------------------
  /**
   * Job identifier
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false, precision = 18, scale = 0)
  private Long id;
  /** jpaVersion gives the current version number for that entity (used by pessimistic / optimistic locking in JPA) */
  @Version()
  @Column(name = "OPTLOCK")
  protected int jpaVersion;
  /**
   * Root Context reference (No cascade at all to have unary operation)
   */
  @ManyToOne
  @JoinColumn(name = "parentId", referencedColumnName = "id", nullable = false)
  private RootContext parent;
  /**
   * Name of this task (useful to process task events)
   */
  @Basic(optional = false)
  @Column(name = "name", nullable = false)
  private String name;
  /**
   * Attribute description :
   * A description of this context / job.
   */
  @Basic(fetch = FetchType.LAZY, optional = false)
  @Lob
  @Column(name = "description", nullable = false)
  private String description;
  /**
   * Job creation date
   */
  @Basic(optional = false)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creationDate", nullable = true)
  private Date creationDate;
  /**
   * Job queue date
   */
  @Basic(optional = true)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "queueDate", nullable = true)
  private Date queueDate = null;
  /**
   * Job run date
   */
  @Basic(optional = true)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "runDate", nullable = true)
  private Date runDate = null;
  /**
   * Job end date
   */
  @Basic(optional = true)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "endDate", nullable = true)
  private Date endDate = null;
  /**
   * Job duration
   */
  @Basic(optional = true)
  @Column(name = "duration", nullable = false)
  private long duration = 0L;
  /**
   * Job state
   */
  @Basic(optional = true)
  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = true)
  private RunState state;
  
  /**
   * Collection result :
   * The collection of Snapshots that are the individual results as function of time of a Simulation or other SimDB Experiment.
   * (
   * Multiplicity : 0..*
   * )
   */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "container")
   private List<ParameterSetting> parameters = null;

  /**
   * Ring Buffer for logs
   */
  @Transient
  private RingBuffer ring = null;

  /**
   * Creates a new RunContext object for JPA
   */
  public RunContext() {
  }

  /**
   * Creates a new RunContext object
   *
   * @param parent root context
   * @param applicationName operation name
   * @param id job identifier
   */
  public RunContext(final RootContext parent, final String applicationName, final Long id) {
    this.parent = parent;
    this.id = id;
    this.name = applicationName;
    // init :
    this.state = RunState.STATE_UNKNOWN;
    this.creationDate = new Date();

    if (parent != null) {
      parent.addChild(this);
    }
  }

  /**
   * Clones this instance via standard java Cloneable support
   *
   * @return cloned instance
   *
   * @throws CloneNotSupportedException
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Returns Jpa version for optimistic locking
   * @return jpa version number
   */
  protected int getJpaVersion() {
    return this.jpaVersion;
  }

  /**
   * Set Jpa version for optimistic locking
   * @param newValue jpa version number
   */
  protected void setJpaVersion(final int newValue) {
    this.jpaVersion = newValue;
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
   * Simple toString representation : "job[id][state] duration ms. - work dir : [workingDir]"
   *
   * @return "job[id][state] duration ms. - work dir : [workingDir]"
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getId() + "][" + getJpaVersion() + "][" + getState() + "] " +
        ((getDuration() > 0L) ? (" : " + getDuration() + " ms.") : "") +
        " - work dir : " + getWorkingDir();
  }

  /**
   * Simple toString representation : "job[id][state]"
   *
   * @return "job[id][state]"
   */
  public String shortString() {
    return getClass().getSimpleName() + "[" + getId() + "][" + getJpaVersion() + "][" + getState() + "]";
  }


  public final RootContext getParent() {
    return parent;
  }

  /**
   * Returns the job identifier
   *
   * @return identifier
   */
  public final Long getId() {
    return id;
  }

  /**
   * Set the job identifier
   *
   * @param pId identifier
   */
  protected final void setId(final Long pId) {
    id = pId;
  }

  /**
   * Returns the job state
   *
   * @return job state
   */
  public final RunState getState() {
    return state;
  }

  public final boolean isRunning() {
    return getState() == RunState.STATE_RUNNING;
  }

  public final boolean isPending() {
    return getState() == RunState.STATE_PENDING;
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
        setQueueDate(new Date());

        break;

      case STATE_RUNNING:
        setRunDate(new Date());

        break;

      case STATE_CANCELLED:
      case STATE_INTERRUPTED:
      case STATE_KILLED:
      case STATE_FINISHED_ERROR:
      case STATE_FINISHED_OK:
        setEndDate(new Date());

        break;

      default:
    }
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public final String getCreationDateFormatted() {
    return TypeWrapper.getInternationalFormat(creationDate);
  }

  /**
   * Returns the job queue date
   *
   * @return job queue date
   */
  public final Date getQueueDate() {
    return queueDate;
  }

  /**
   * Defines the job queue date
   *
   * @param queueDate date to set
   */
  private final void setQueueDate(final Date queueDate) {
    this.queueDate = queueDate;
  }

  public final String getQueueDateFormatted() {
    if (queueDate == null) {
      return "-";
    }
    return TypeWrapper.getInternationalFormat(queueDate);
  }

  /**
   * Returns the job run date
   *
   * @return job run date
   */
  public final Date getRunDate() {
    return runDate;
  }

  /**
   * Defines the job run date
   *
   * @param runDate run date to set
   */
  private final void setRunDate(final Date runDate) {
    this.runDate = runDate;
  }

  public final String getRunDateFormatted() {
    if (runDate == null) {
      return "-";
    }
    return TypeWrapper.getInternationalFormat(runDate);
  }

  /**
   * Returns the job end date
   *
   * @return job end date
   */
  public final Date getEndDate() {
    return endDate;
  }

  /**
   * Defines the job end date
   *
   * @param endDate date to set
   */
  private final void setEndDate(final Date endDate) {
    this.endDate = endDate;
  }

  public final String getEndDateFormatted() {
    if (endDate == null) {
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
  public final RingBuffer getRing() {
    return ring;
  }

  /**
   * Defines the ring buffer
   *
   * @param ring buffer to set
   */
  public final void setRing(final RingBuffer ring) {
    this.ring = ring;
  }

  /**
   * Return the name of this context
   * @return name of this context
   */
  public final String getName() {
    return name;
  }

  /**
   * Return the description of this context
   * @return description of this context
   */
  public final String getDescription() {
    return description;
  }

  public List<ParameterSetting> getParameters() {
	return parameters;
  }

  public void setParameters(List<ParameterSetting> parameters) {
	this.parameters = parameters;
  }
  
  public void addParameter(ParameterSetting parameter)
  {
	if(parameters == null)
		parameters = new ArrayList<ParameterSetting>();
	parameters.add(parameter);
	parameter.setContainer(this);
  }


}
