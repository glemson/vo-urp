package org.ivoa.util.runner;


/**
 * Job States
 * 
 * @author laurent bourges (voparis)
 */
public enum RunState {
  /** undefined state */
  STATE_UNKNOWN,
  /** pending state */
  STATE_PENDING,
  /** running state */
  STATE_RUNNING,
  /** finished state with an error */
  STATE_FINISHED_ERROR,
  /** finished state */
  STATE_FINISHED_OK,  
  /** cancelled state */
  STATE_CANCELLED  
  }
