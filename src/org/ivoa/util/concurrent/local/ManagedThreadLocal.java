package org.ivoa.util.concurrent.local;

import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * This class enhances the ThreadLocal class to define initialization and shutdown events
 * 
 * @param <T> ThreadLocal Type used
 * @see org.ivoa.bean.LogSupport
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ManagedThreadLocal<T> extends ThreadLocal<T> {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /**
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();

  // ~ Constructors
  // --------------------------------------------------------------------------------------------------------

  /**
   * Protected constructor
   */
  public ManagedThreadLocal() {
    super();
  }

  // ~ Methods
  // --------------------------------------------------------------------------------------------------------

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the initialValue() event for this ManagedThreadLocal instance
   * 
   * @see ThreadLocal#initialValue()
   * @throws IllegalStateException if a problem occurred
   */
  public void onInitialValue() throws IllegalStateException {
    /* no-op */
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the remove() event for this ManagedThreadLocal instance
   * 
   * @see ThreadLocal#remove()
   * @throws IllegalStateException if a problem occurred
   */
  public void onRemoveValue() throws IllegalStateException {
    /* no-op */
  }

  // ~ End of file
  // --------------------------------------------------------------------------------------------------------
}
