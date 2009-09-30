package org.ivoa.util.runner;

/**
 * This interface handle job and queue events.
 * @see RunState
 * 
 * @author laurent bourges (voparis)
 */
public interface JobListener {
  
  /**
   * Perform the job event from the given root context.
   * Called when the job has the following transitions : pending / running / finished
   * @param rootCtx root context
   */
  public void performJobEvent(final RootContext rootCtx);

  /**
   * Perform the task event from the given run context =
   * @param rootCtx root context
   * @param runCtx  current run context
   */
  public void performTaskEvent(final RootContext rootCtx, final RunContext runCtx);

  /**
   * Perform the event from the given run context
   * @param rootCtx root context
   * @param runCtx  current run context
   * @return boolean: true of the processing should continue, false if the job should be terminated
   */
  public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx);
  
}
