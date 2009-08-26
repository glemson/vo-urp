package org.ivoa.util.runner.process;

import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;

import java.util.Arrays;


/**
 * Unix Process Job (command, buffer, process status, unix process wrapper)
 *
 * @author laurent bourges (voparis)
 */
public final class ProcessContext extends RunContext {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * Command array [unix command, arguments]
   */
  private final String[] command;

  /**
   * Process status
   */
  private int status = -1;

  /** child UNIX process */
  private Process process = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new ProcessContext object
   *
   * @param id job identifier
   * @param cmd command array
   * @param workingDir process working directory
   */
  public ProcessContext(final RootContext parent, final String name, final Integer id, final String[] cmd) {
    super(parent, name, id);
    this.command = cmd;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * this method destroys the child UNIX process
   * @see ProcessRunner#stop(ProcessContext)
   */
  @Override
  public void kill() {
    // java process is killed => unix process is killed :
    ProcessRunner.stop(this);
  }

  /**
   * Simple toString representation : "job[id][state] duration ms. {command} - dir : workDir"
   *
   * @return "job[id][state] duration ms. {command} - dir : workDir"
   */
  @Override
  public String toString() {
    return super.toString() + " {" + Arrays.toString(getCommand()) + "}";
  }

  /**
   * Returns the command array
   *
   * @return command array
   */
  public String[] getCommand() {
    return command;
  }


  /**
   * Returns the Unix status or -1 if undefined
   *
   * @return Unix status or -1 if undefined
   */
  public int getStatus() {
    return status;
  }

  /**
   * Defines the Unix status
   *
   * @param status status to set
   */
  protected void setStatus(final int status) {
    this.status = status;
  }

  /**
   * Returns the UNIX Process
   *
   * @return UNIX Process
   */
  protected Process getProcess() {
    return process;
  }

  /**
   * Defines the UNIX Process
   *
   * @param process UNIX Process
   */
  protected void setProcess(final Process process) {
    this.process = process;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
