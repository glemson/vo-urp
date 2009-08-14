package org.ivoa.util.text;

import org.ivoa.util.concurrent.ManagedThreadLocal;


/**
 * This class uses the ThreadLocal pattern to associate a StringBuilder to the current thread
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class StringBuilderThreadLocal extends ManagedThreadLocal<StringBuilderContext> {

  // ~ Constructors
  // ---------------------------------------------------------------------------------------------------
  /**
   * Protected constructor
   */
  protected StringBuilderThreadLocal() {
    super();
  }

  // ~ Methods
  // --------------------------------------------------------------------------------------------------------
  /**
   * Return a new StringBuilder for the current thread
   *
   * @return new StringBuilder instance with initial capacity set to CAPACITY
   */
  @Override
  protected final StringBuilderContext onInitialValue() {
    return new StringBuilderContext();
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the remove() event for this StringBuilderThreadLocal instance
   *
   * @see ThreadLocal#remove()
   * @param value T value to clear
   * @return true if the value can be removed from the thread local map
   * @throws IllegalStateException if a problem occurred
   */
  @Override
  protected boolean onRemoveValue(final StringBuilderContext value) throws IllegalStateException {
    // force GC :
    value.clear();

    return true;
  }
}

