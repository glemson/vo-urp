package org.ivoa.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * This class enhances the ThreadLocal class to define initialization and shutdown events
 * 
 * @param <T> ThreadLocal Type used
 * @see org.ivoa.bean.LogSupport
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ManagedThreadLocal<T> extends ThreadLocal<T> {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** Diagnostics flag to see ThreadLocal usage = initialValue() and remove() calls */
  private final boolean DIAGNOSTICS = false;

  /**
   * Logger for the base framework
   * 
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------
  /** ThreadLocal name */
  private final String name;

  /** creation counter */
  private final AtomicInteger createCounter = new AtomicInteger();

  /** remove counter */
  private final AtomicInteger removeCounter = new AtomicInteger();

  // ~ Constructors
  // --------------------------------------------------------------------------------------------------------
  /**
   * Protected constructor
   */
  public ManagedThreadLocal() {
    super();
    this.name = getClass().getSimpleName();
  }

  // ~ Methods
  // --------------------------------------------------------------------------------------------------------
  /**
   * Returns the current thread's "initial value" for this thread-local variable.
   * 
   * @see ThreadLocal#initialValue()
   * @return new T instance or null if a runtime exception occured
   */
  @Override
  protected final T initialValue() {
    if (logB.isDebugEnabled()) {
      logB.debug(this.name + ".initialValue : enter");
    }
    T newValue = null;
    try {
      newValue = onInitialValue();
    } catch (final RuntimeException re) {
      logB.error(this.name + ".initialValue : failure : ", re);
    }

    if (DIAGNOSTICS) {
      logB.error(this.name + ".initialValue : caller : ", new Throwable());
    }

    createCounter.incrementAndGet();
    if (logB.isDebugEnabled()) {
      logB.debug(this.name + ".initialValue : exit : " + newValue);
    }
    return newValue;
  }

  /**
   * Removes the current thread's value for this thread-local variable.
   * 
   * @see ThreadLocal#remove()
   */
  @Override
  public final void remove() {
    if (logB.isDebugEnabled()) {
      logB.debug(this.name + ".remove : enter");
    }
    boolean doRemove = false;
    try {
      doRemove = onRemoveValue();
    } catch (final RuntimeException re) {
      logB.error(this.name + ".remove : failure : ", re);
      doRemove = true;
    }
    if (doRemove) {
      super.remove();
    }

    if (DIAGNOSTICS) {
      logB.error(this.name + ".remove : caller : ", new Throwable());
    }

    removeCounter.incrementAndGet();
    if (logB.isDebugEnabled()) {
      logB.debug(this.name + ".remove : exit");
    }
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the initialValue() event for this ManagedThreadLocal instance
   * 
   * @return new T value
   * @see ThreadLocal#initialValue()
   * @throws IllegalStateException if a problem occurred
   */
  protected T onInitialValue() throws IllegalStateException {
    /* no-op */
    return null;
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the remove() event for this ManagedThreadLocal instance
   * 
   * @see ThreadLocal#remove()
   * @return true if the value can be removed from the thread local map
   * @throws IllegalStateException if a problem occurred
   */
  protected boolean onRemoveValue() throws IllegalStateException {
    /* no-op */
    return true;
  }

  /**
   * Dump usage statistics
   */
  public void dumpStatistics() {
    if (logB.isDebugEnabled()) {
      logB.debug(this.name + ".dumpStatistics : creation = " + createCounter.get() + " - remove = " + removeCounter.get());
    }
  }

  // ~ End of file
  // --------------------------------------------------------------------------------------------------------
}
